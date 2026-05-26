package fr.honeygroup.bll.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import fr.honeygroup.enumeration.StatutLead;
import fr.honeygroup.mapper.LeadMapper;
import fr.honeygroup.repository.DemandeLeadRepository;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests des règles métier de LeadService (BLL)")
class LeadServiceImplTest {

    @Mock private DemandeLeadRepository demandeLeadRepository;
    @Mock private PoleRepository poleRepository;
    @Mock private UserRepository userRepository;
    @Mock private LeadMapper leadMapper;

    @InjectMocks
    private LeadServiceImpl leadService;

    private Pole poleValide;
    private User userValide;
    private LeadRequest requestVisiteurNominal;

    @BeforeEach
    void setUp() {
        poleValide = Pole.builder().id(1L).nom("Écotourisme").build();
        userValide = User.builder().id(99L).email("client@honeygroup.fr").build();

        // Initialisation d'une requête visiteur conforme de base
        requestVisiteurNominal = new LeadRequest();
        requestVisiteurNominal.setPoleId(1L);
        requestVisiteurNominal.setNom("Jean Dupont");
        requestVisiteurNominal.setEmail("jean.dupont@test.fr");
        requestVisiteurNominal.setSource("Site Web");
        
        Map<String, String> details = new HashMap<>();
        details.put("destination", "Madagascar");
        requestVisiteurNominal.setSpecificDetails(details);
    }

    // ============================================================================
    // WORKFLOW DE CRÉATION & RÈGLES MÉTIERS (createLead)
    // ============================================================================

    @Test
    @DisplayName("Création Lead : Succès nominal pour un compte visiteur (Anonyme)")
    void createLead_Succes_VisiteurAnonyme() {
        // Arrange
        when(poleRepository.findById(1L)).thenReturn(Optional.of(poleValide));
        when(demandeLeadRepository.save(any(DemandeLead.class))).thenAnswer(inv -> inv.getArgument(0));
        when(leadMapper.toResponse(any(DemandeLead.class))).thenReturn(new LeadResponse());

        // Act
        LeadResponse response = leadService.createLead(requestVisiteurNominal);

        // Assert
        assertNotNull(response);
        verify(userRepository, never()).findById(any());
        verify(demandeLeadRepository, times(1)).save(any(DemandeLead.class));
    }

    @Test
    @DisplayName("Création Lead : Succès nominal pour un utilisateur connecté (Polymorphisme)")
    void createLead_Succes_UtilisateurConnecte() {
        // Arrange
        requestVisiteurNominal.setUserId(99L);
        requestVisiteurNominal.setNom(null);  // Optionnel si connecté
        requestVisiteurNominal.setEmail(null); // Optionnel si connecté

        when(userRepository.findById(99L)).thenReturn(Optional.of(userValide));
        when(poleRepository.findById(1L)).thenReturn(Optional.of(poleValide));
        when(demandeLeadRepository.save(any(DemandeLead.class))).thenAnswer(inv -> inv.getArgument(0));
        when(leadMapper.toResponse(any(DemandeLead.class))).thenReturn(new LeadResponse());

        // Act
        LeadResponse response = leadService.createLead(requestVisiteurNominal);

        // Assert
        assertNotNull(response);
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Création Lead : Échec si le pôle d'activité est introuvable")
    void createLead_PoleIntrouvable_LanceException() {
        // Arrange
        when(poleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> leadService.createLead(requestVisiteurNominal));
        assertEquals("Pôle introuvable", exception.getMessage());
    }

    @Test
    @DisplayName("Règle Métier 1 : Exception si le nom du visiteur est absent")
    void createLead_VisiteurSansNom_LanceException() {
        requestVisiteurNominal.setNom("");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> leadService.createLead(requestVisiteurNominal));
        assertEquals("Le nom est obligatoire pour un visiteur", exception.getMessage());
    }

    @Test
    @DisplayName("Règle Métier 1 : Exception si l'email du visiteur est absent ou mal formé")
    void createLead_VisiteurEmailInvalide_LanceException() {
        // Cas absent
        requestVisiteurNominal.setEmail(null);
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> leadService.createLead(requestVisiteurNominal));
        assertEquals("L'email est obligatoire pour un visiteur", ex1.getMessage());

        // Cas mal formé
        requestVisiteurNominal.setEmail("adresseInvalide.fr");
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> leadService.createLead(requestVisiteurNominal));
        assertEquals("Email invalide", ex2.getMessage());
    }

    @Test
    @DisplayName("Règle Métier 2 : Exception si aucun détail de qualification n'est fourni")
    void createLead_SansDetails_LanceException() {
        requestVisiteurNominal.setSpecificDetails(null);
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> leadService.createLead(requestVisiteurNominal));
        assertEquals("Les détails sont obligatoires", ex1.getMessage());

        requestVisiteurNominal.setSpecificDetails(new HashMap<>());
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> leadService.createLead(requestVisiteurNominal));
        assertEquals("Les détails sont obligatoires", ex2.getMessage());
    }

    @Test
    @DisplayName("Règle Métier 3 : Protection Anti-Spam si le payload contient plus de 20 critères")
    void createLead_TropDeChampsDetails_LanceException() {
        Map<String, String> spamDetails = new HashMap<>();
        for (int i = 1; i <= 21; i++) {
            spamDetails.put("cle" + i, "valeur" + i);
        }
        requestVisiteurNominal.setSpecificDetails(spamDetails);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> leadService.createLead(requestVisiteurNominal));
        assertEquals("Trop de champs envoyés", exception.getMessage());
    }

    // ============================================================================
    // WORKFLOWS DE CONSULTATION & RECHERCHE
    // ============================================================================

    @Test
    @DisplayName("Consultation : Récupération exhaustive de tous les leads")
    void getAllLeads_Succes() {
        // Arrange
        when(demandeLeadRepository.findAll()).thenReturn(List.of(new DemandeLead(), new DemandeLead()));
        when(leadMapper.toResponse(any(DemandeLead.class))).thenReturn(new LeadResponse());

        // Act
        List<LeadResponse> result = leadService.getAllLeads();

        // Assert
        assertEquals(2, result.size());
        verify(demandeLeadRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Consultation : Récupération par ID et exception si inexistant")
    void getLeadById_Combinaisons() {
        // Cas nominal
        DemandeLead lead = new DemandeLead();
        when(demandeLeadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(leadMapper.toResponse(lead)).thenReturn(new LeadResponse());
        assertNotNull(leadService.getLeadById(1L));

        // Cas d'échec
        when(demandeLeadRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> leadService.getLeadById(99L));
    }

    // ============================================================================
    // WORKFLOWS DE MUTATION & SUPPRESSION (Update, Delete)
    // ============================================================================

    @Test
    @DisplayName("Mutation : Changement de statut avec persistance forcée")
    void updateLeadStatus_Succes() {
        // Arrange
        DemandeLead lead = new DemandeLead();
        lead.setStatut(StatutLead.NOUVEAU);

        when(demandeLeadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(demandeLeadRepository.save(any(DemandeLead.class))).thenAnswer(inv -> inv.getArgument(0));
        when(leadMapper.toResponse(any(DemandeLead.class))).thenReturn(new LeadResponse());

        // Act
        leadService.updateLeadStatus(1L, StatutLead.TRAITE);

        // Assert
        assertEquals(StatutLead.TRAITE, lead.getStatut());
        verify(demandeLeadRepository, times(1)).save(lead);
    }

    @Test
    @DisplayName("Suppression : Purge définitive si l'ID existe ou levée d'erreur")
    void deleteLead_Combinaisons() {
        // Cas nominal
        when(demandeLeadRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> leadService.deleteLead(1L));
        verify(demandeLeadRepository, times(1)).deleteById(1L);

        // Cas d'échec
        when(demandeLeadRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> leadService.deleteLead(99L));
    }
}