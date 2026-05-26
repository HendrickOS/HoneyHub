package fr.honeygroup.bll.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.honeygroup.bo.Circuit;
import fr.honeygroup.bo.CoursLangue;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.request.CircuitRequest;
import fr.honeygroup.bo.request.CoursLangueRequest;
import fr.honeygroup.bo.request.PrestationRequest;
import fr.honeygroup.bo.response.PrestationResponse;
import fr.honeygroup.enumeration.StatutPrestation;
import fr.honeygroup.mapper.PrestationMapper;
import fr.honeygroup.repository.CircuitRepository;
import fr.honeygroup.repository.CoursLangueRepository;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.repository.PrestationRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests des règles métier de PrestationService (BLL)")
class PrestationServiceImplTest {

    @Mock private PrestationRepository prestationRepository;
    @Mock private CircuitRepository circuitRepository;
    @Mock private CoursLangueRepository coursLangueRepository;
    @Mock private PoleRepository poleRepository;
    @Mock private PrestationMapper prestationMapper;

    @InjectMocks
    private PrestationServiceImpl prestationService;

    private Pole poleEcotourisme;
    private Prestation prestationGenerique;
    private PrestationRequest genericRequest;

    @BeforeEach
    void setUp() {
        poleEcotourisme = Pole.builder().id(1L).nom("Écotourisme").build();

        prestationGenerique = new Prestation();
        prestationGenerique.setId(10L);
        prestationGenerique.setTitreService("Prestation Générique Test");
        prestationGenerique.setPrixBase(150.00);
        prestationGenerique.setStatut(StatutPrestation.ACTIF);
        prestationGenerique.setMetadata(new HashMap<>());

        genericRequest = new PrestationRequest();
        genericRequest.setPoleId(1L);
        genericRequest.setTitreService("Nouvelle Offre");
        genericRequest.setDescription("Description de l'offre");
        genericRequest.setPrixBase(200.0);
    }

    // ============================================================================
    // WORKFLOWS DE LECTURE & FILTRES (getAll, getById, findByTrajet)
    // ============================================================================

    @Test
    @DisplayName("Extraction globale : Récupération et conversion polymorphe de toutes les prestations")
    void getAllPrestations_Succes() {
        when(prestationRepository.findAll()).thenReturn(List.of(prestationGenerique, new Circuit()));
        when(prestationMapper.toGenericResponse(any(Prestation.class))).thenReturn(new PrestationResponse());

        List<PrestationResponse> results = prestationService.getAllPrestations();

        assertNotNull(results);
        assertEquals(2, results.size());
        verify(prestationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Recherche par ID : Renvoie la réponse si présente ou lève une exception si absente")
    void getPrestationById_Combinaisons() {
        // Cas nominal
        when(prestationRepository.findById(10L)).thenReturn(Optional.of(prestationGenerique));
        when(prestationMapper.toGenericResponse(prestationGenerique)).thenReturn(new PrestationResponse());
        assertNotNull(prestationService.getPrestationById(10L));

        // Cas d'échec
        when(prestationRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> prestationService.getPrestationById(99L));
        assertEquals("Prestation introuvable", exception.getMessage());
    }

    @Test
    @DisplayName("Recherche par trajet : Renvoie les offres filtrées géographiquement")
    void findByTrajet_Succes() {
        when(prestationRepository.findByTrajet("Paris", "Antananarivo")).thenReturn(List.of(prestationGenerique));
        when(prestationMapper.toGenericResponse(prestationGenerique)).thenReturn(new PrestationResponse());

        List<PrestationResponse> result = prestationService.findByTrajet("Paris", "Antananarivo");

        assertEquals(1, result.size());
        verify(prestationRepository, times(1)).findByTrajet("Paris", "Antananarivo");
    }

    // ============================================================================
    // MANIPULATION DES MÉTADONNÉES DYNAMIQUES (addOrUpdateMetadata)
    // ============================================================================

    @Test
    @DisplayName("Métadonnées : Injection ou mise à jour d'un attribut JSON et persistance")
    void addOrUpdateMetadata_Succes() {
        when(prestationRepository.findById(10L)).thenReturn(Optional.of(prestationGenerique));
        when(prestationRepository.save(prestationGenerique)).thenReturn(prestationGenerique);

        prestationService.addOrUpdateMetadata(10L, "guide", "Local Expert");

        assertEquals("Local Expert", prestationGenerique.getMetadata().get("guide"));
        verify(prestationRepository, times(1)).save(prestationGenerique);
    }

    @Test
    @DisplayName("Métadonnées : Exception si la prestation ciblée n'existe pas")
    void addOrUpdateMetadata_PrestationAbsente_LanceException() {
        when(prestationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> prestationService.addOrUpdateMetadata(99L, "clé", "valeur"));
        verify(prestationRepository, never()).save(any());
    }

    // ============================================================================
    // CRÉATIONS POLYMORPHES (Générique, Circuit, Cours de langue)
    // ============================================================================

    @Test
    @DisplayName("Création Générique : Succès et application du statut ACTIF par défaut si omis")
    void createPrestationGenerique_Succes_AppliqueStatutParDefaut() {
        genericRequest.setStatut(null); // Statut absent de la requête
        when(poleRepository.findById(1L)).thenReturn(Optional.of(poleEcotourisme));
        when(prestationRepository.save(any(Prestation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(prestationMapper.toGenericResponse(any(Prestation.class))).thenReturn(new PrestationResponse());

        prestationService.createPrestationGenerique(genericRequest);

        verify(prestationRepository, times(1)).save(argThat(prestation -> 
            prestation.getStatut() == StatutPrestation.ACTIF && 
            prestation.getPole().equals(poleEcotourisme)
        ));
    }

    @Test
    @DisplayName("Création Circuit : Hydratation correcte du socle commun et des champs de logistique")
    void createCircuit_Succes() {
        CircuitRequest request = new CircuitRequest();
        request.setPoleId(1L);
        request.setTitreService("Safari");
        request.setStatut(StatutPrestation.EN_ATTENTE);
        request.setDescriptionLongue("Description complète du safari");
        request.setItineraire("Parc National A -> Parc B");
        request.setDuree("7");

        when(poleRepository.findById(1L)).thenReturn(Optional.of(poleEcotourisme));
        when(circuitRepository.save(any(Circuit.class))).thenAnswer(inv -> inv.getArgument(0));
        when(prestationMapper.toGenericResponse(any(Circuit.class))).thenReturn(new PrestationResponse());

        prestationService.createCircuit(request);

        verify(circuitRepository, times(1)).save(argThat(circuit -> 
            "Safari".equals(circuit.getTitreService()) &&
            StatutPrestation.EN_ATTENTE == circuit.getStatut() &&
            "Parc National A -> Parc B".equals(circuit.getItineraire()) &&
            Integer.valueOf(7).equals(circuit.getDuree())
        ));
    }

    @Test
    @DisplayName("Création Cours de langue : Hydratation correcte du socle et du programme pédagogique")
    void createCoursLangue_Succes() {
        CoursLangueRequest request = new CoursLangueRequest();
        request.setPoleId(1L);
        request.setTitreService("Malagasy Intensif");
        request.setLangue("Malagasy");
        request.setNiveau("B1");
        request.setDescriptifProgramme("Vocabulaire axé écotourisme");

        when(poleRepository.findById(1L)).thenReturn(Optional.of(poleEcotourisme));
        when(coursLangueRepository.save(any(CoursLangue.class))).thenAnswer(inv -> inv.getArgument(0));
        when(prestationMapper.toGenericResponse(any(CoursLangue.class))).thenReturn(new PrestationResponse());

        prestationService.createCoursLangue(request);

        verify(coursLangueRepository, times(1)).save(argThat(cours -> 
            "Malagasy Intensif".equals(cours.getTitreService()) &&
            "Malagasy".equals(cours.getLangue()) &&
            "B1".equals(cours.getNiveau())
        ));
    }

    @Test
    @DisplayName("Création : Levée d'exception immédiate si le pôle rattaché est introuvable")
    void createPrestation_PoleIntrouvable_LanceException() {
        when(poleRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            prestationService.createPrestationGenerique(genericRequest)
        );
        assertEquals("Pole introuvable", exception.getMessage());
        verify(prestationRepository, never()).save(any());
    }

    // ============================================================================
    // WORKFLOW DE SUPPRESSION (deletePrestation)
    // ============================================================================

    @Test
    @DisplayName("Suppression : Suppression physique si présente ou erreur si ID inexistant")
    void deletePrestation_Combinaisons() {
        // Cas nominal
        when(prestationRepository.existsById(10L)).thenReturn(true);
        assertDoesNotThrow(() -> prestationService.deletePrestation(10L));
        verify(prestationRepository, times(1)).deleteById(10L);

        // Cas d'échec
        when(prestationRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> prestationService.deletePrestation(99L));
    }
}