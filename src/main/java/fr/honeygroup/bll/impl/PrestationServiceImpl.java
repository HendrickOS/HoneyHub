package fr.honeygroup.bll.impl;

import enumeration.StatutPrestation;
import fr.honeygroup.bll.PrestationService;
import fr.honeygroup.bo.Circuit;
import fr.honeygroup.bo.CoursLangue;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.request.CircuitRequest;
import fr.honeygroup.bo.request.CoursLangueRequest;
import fr.honeygroup.bo.request.PrestationRequest;
import fr.honeygroup.bo.response.PrestationResponse;
import fr.honeygroup.mapper.PrestationMapper;
import fr.honeygroup.repository.CircuitRepository;
import fr.honeygroup.repository.CoursLangueRepository;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.repository.PrestationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation concrete du service de gestion du catalogue des prestations.
 * <p>
 * Cette classe orchestre la logique metier polymorphe liee aux offres de Honey Group.
 * Elle interagit avec les differents depots (Repositories) dedies aux sous-types 
 * afin de garantir la bonne persistance de l'heritage relationnel (strategy JOINED ou SINGLE_TABLE).
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PrestationServiceImpl implements PrestationService {

    /** Depot associe a l'entite racine Prestation. */
    private final PrestationRepository prestationRepository;
    
    /** Depot specifique destine aux offres de type Circuit. */
    private final CircuitRepository circuitRepository;
    
    /** Depot specifique destine aux offres de type Cours de langue. */
    private final CoursLangueRepository coursLangueRepository;
    
    /** Referentiel d'acces aux poles organisationnels. */
    private final PoleRepository poleRepository;
    
    /** Composant MapStruct dedie a la conversion unifiee des entites vers les DTOs de reponse. */
    private final PrestationMapper prestationMapper;

    /**
     * {@inheritDoc}
     * <p>
     * Extrait toutes les prestations via l'API Stream de Java et applique une conversion 
     * unifiee et polymorphe grace a l'infrastructure MapStruct.
     * </p>
     */
    @Override
    public List<PrestationResponse> getAllPrestations() {
        return prestationRepository.findAll().stream()
                .map(prestationMapper::toGenericResponse)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Localise une prestation par sa cle primaire ou leve un echec metier si absente.
     * </p>
     * * @throws RuntimeException Si l'identifiant ne correspond a aucune prestation en base.
     */
    @Override
    public PrestationResponse getPrestationById(Long id) {
        Prestation prestation = prestationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestation introuvable"));
        return prestationMapper.toGenericResponse(prestation);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Cree une offre standard sans attributs derives. L'operation est encapsulee
     * dans une transaction d'ecriture pour securiser la liaison avec l'entite Pole.
     * </p>
     */
    @Override
    @Transactional
    public PrestationResponse createPrestationGenerique(PrestationRequest request) {
        Pole pole = getPole(request.getPoleId());
        
        Prestation prestation = new Prestation();
        mapCommonFields(prestation, request, pole);
        
        return prestationMapper.toGenericResponse(prestationRepository.save(prestation));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Met en oeuvre la creation d'un sous-type Circuit. Hydrate les donnees du socle 
     * commun via une routine privee avant d'injecter les specifications logistiques 
     * et de sauvegarder via le depot dedie.
     * </p>
     */
    @Override
    @Transactional
    public PrestationResponse createCircuit(CircuitRequest request) {
        Pole pole = getPole(request.getPoleId());
        
        Circuit circuit = new Circuit();
        mapCommonFields(circuit, request, pole);
        circuit.setDescriptionLongue(request.getDescriptionLongue());
        circuit.setItineraire(request.getItineraire());
        circuit.setDuree(request.getDuree());
        
        return prestationMapper.toGenericResponse(circuitRepository.save(circuit));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Met en oeuvre la creation d'un sous-type CoursLangue. Hydrate les donnees de base 
     * puis y greffe les contraintes et programmes pedagogiques avant la persistance.
     * </p>
     */
    @Override
    @Transactional
    public PrestationResponse createCoursLangue(CoursLangueRequest request) {
        Pole pole = getPole(request.getPoleId());
        
        CoursLangue coursLangue = new CoursLangue();
        mapCommonFields(coursLangue, request, pole);
        coursLangue.setLangue(request.getLangue());
        coursLangue.setNiveau(request.getNiveau());
        coursLangue.setDescriptifProgramme(request.getDescriptifProgramme());
        
        return prestationMapper.toGenericResponse(coursLangueRepository.save(coursLangue));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Supprime de maniere transactionnelle l'offre specifiee apres verification de sa presence.
     * </p>
     * * @throws RuntimeException Si l'ID technique fourni ne correspond a aucune entree.
     */
    @Override
    @Transactional
    public void deletePrestation(Long id) {
        if (!prestationRepository.existsById(id)) {
            throw new RuntimeException("Prestation introuvable");
        }
        prestationRepository.deleteById(id);
    }

    /**
     * Routine interne d'extraction et de validation de la presence d'un pole.
     * * @param poleId L'ID technique du pole demande.
     * @return L'entite structurelle Pole correspondante.
     * @throws RuntimeException Si le pole specifie n'existe pas.
     */
    private Pole getPole(Long poleId) {
        return poleRepository.findById(poleId)
                .orElseThrow(() -> new RuntimeException("Pole introuvable"));
    }

    /**
     * Encapsule le mapping des attributs transverses partages par l'ensemble des prestations.
     * <p>
     * Securise le statut de l'offre en y appliquant une valeur active par defaut s'il est omis.
     * </p>
     * * @param prestation L'entite cible (generique ou derivee) a hydrater.
     * @param request Le DTO source contenant les parametres d'entree.
     * @param pole Le pole d'activite a lier.
     */
    private void mapCommonFields(Prestation prestation, PrestationRequest request, Pole pole) {
        prestation.setPole(pole);
        prestation.setTitreService(request.getTitreService());
        prestation.setDescription(request.getDescription());
        prestation.setPrixBase(request.getPrixBase());
        prestation.setStatut(request.getStatut() != null ? request.getStatut() : StatutPrestation.ACTIF);
    }
}