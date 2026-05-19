package fr.honeygroup.bll.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.honeygroup.bll.LeadService;
import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.DetailsSpecifiques;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import fr.honeygroup.mapper.LeadMapper;
import fr.honeygroup.repository.DemandeLeadRepository;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Implémentation du service métier gérant le flux d'acquisition des opportunités d'affaires (Lead).
 * <p>
 * Cette classe orchestre la capture des demandes d'informations ou expressions de besoins 
 * formulées par les prospects, applique une validation sur les critères requis, et construit 
 * l'arborescence relationnelle dynamique pour les détails sur-mesure (approche NoSQL/EAV).
 * </p>
 */
@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final DemandeLeadRepository demandeLeadRepository;
    private final PoleRepository poleRepository;
    private final UserRepository userRepository;
    private final LeadMapper leadMapper;

    /**
     * Crée et enregistre un nouveau dossier de prospection (Lead) avec ses spécifications personnalisées.
     * <p>
     * La méthode valide la présence de critères optionnels, associe l'utilisateur demandeur ainsi 
     * que le pôle ciblé, puis convertit la structure clé/valeur reçue en entités de 
     * détails persistables rattachées bidirectionnellement au lead parent.
     * </p>
     * @param request Objet DTO contenant l'identité du prospect, le pôle d'activité et la cartographie des besoins spécifiques.
     * @return Le {@link LeadResponse} enrichi et converti après insertion réussie en base de données.
     * @throws RuntimeException Si la structure des détails est vide ou absente, ou si l'utilisateur ou le pôle 
     * cibles s'avèrent introuvables.
     */
    @Override
    @Transactional
    public LeadResponse createLead(LeadRequest request) {

        // Validation défensive des données complémentaires soumises par le formulaire
        validateDetails(request.getSpecificDetails());

        // Chargement du compte client associé à l'opportunité commerciale
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User introuvable"));

        //a voir
        /* Prestation prestation = prestationRepository.findById(request.getPrestationId())
                .orElseThrow(() -> new RuntimeException("Prestation introuvable"));    */

        Pole pole = poleRepository.findById(request.getPoleId())
                .orElseThrow(() -> new RuntimeException("Pôle introuvable"));
        
        // 🟢 création lead
        DemandeLead lead = DemandeLead.builder()
                .user(user)
              //.prestation(prestation)
                .pole(pole)
                .source(request.getSource())
                .build();

        // Mapping et pivotement fonctionnel du dictionnaire de clés/valeurs vers la table des spécifications relationnelles
        List<DetailsSpecifiques> detailsList = request.getSpecificDetails()
                .entrySet()
                .stream()
                .map(entry -> DetailsSpecifiques.builder()
                        .champCle(entry.getKey())
                        .valeur(entry.getValue())
                        .demandeLead(lead) // Association de la clé étrangère bidirectionnelle
                        .build()
                )
                .toList();

        // Injection de la collection de spécifications qualifiées au sein de l'agrégat parent
        lead.setSpecificDetails(detailsList);

        // Persistance de l'arbre d'entités complet (la cascade propage l'enregistrement des lignes détails rattachées)
        DemandeLead saveDemandeLead = demandeLeadRepository.save(lead);

        // Transformation et extraction sécurisée vers le format de réponse de l'API
        return leadMapper.toResponse(saveDemandeLead);
    }

    /**
     * Règle de validation de cohérence interne.
     * <p>
     * Bloque le traitement en amont si aucune caractéristique ou note spécifique n'accompagne 
     * l'expression de besoin sur-mesure du formulaire de contact.
     * </p>
     * @param details La cartographie dictionnaire des critères techniques ou organisationnels fournis.
     * @throws RuntimeException Si le dictionnaire s'avère nul ou ne contient aucun élément.
     */
    private void validateDetails(Map<String, String> details) {
        if (details == null || details.isEmpty()) {
            throw new RuntimeException("Details obligatoires");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Extrait l'ensemble des dossiers de prospection enregistrés et délègue la transformation 
     * de la collection d'entités vers la liste de DTOs via l'infrastructure du LeadMapper.
     * </p>
     */
    @Override
    public List<LeadResponse> getAllLeads() {
        return demandeLeadRepository.findAll().stream()
                .map(leadMapper::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Extrait un lead précis par sa clé primaire technique ou lève une exception si la ligne est introuvable.
     * </p>
     * @throws RuntimeException Si l'identifiant technique spécifié n'existe pas en base de données.
     */
    @Override
    public LeadResponse getLeadById(Long id) {
        return demandeLeadRepository.findById(id)
                .map(leadMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Lead introuvable"));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Modifie l'état du lead de manière transactionnelle. Cette opération permet de piloter 
     * l'évolution du prospect à travers les échelons du tunnel de conversion commerciale.
     * </p>
     * @throws RuntimeException Si l'ID du lead ciblé pour la transition de statut n'existe pas.
     */
    @Override
    @Transactional
    public LeadResponse updateLeadStatus(Long id, enumeration.StatutLead statut) {
        DemandeLead lead = demandeLeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead introuvable"));
        lead.setStatut(statut);
        return leadMapper.toResponse(demandeLeadRepository.save(lead));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Supprime définitivement un lead et purge en cascade l'ensemble des lignes de détails 
     * spécifiques associées pour maintenir la propreté de la base de données.
     * </p>
     * @throws RuntimeException Si la ressource ciblée pour la suppression n'est pas répertoriée.
     */
    @Override
    @Transactional
    public void deleteLead(Long id) {
        if (!demandeLeadRepository.existsById(id)) {
            throw new RuntimeException("Lead introuvable");
        }
        demandeLeadRepository.deleteById(id);
    }
}