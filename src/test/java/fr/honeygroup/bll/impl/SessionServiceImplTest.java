package fr.honeygroup.bll.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.request.SessionRequest;
import fr.honeygroup.bo.response.SessionResponse;
import fr.honeygroup.enumeration.StatutSession;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessLogicException;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException;
import fr.honeygroup.exception.GlobalExceptionHandler.SessionCapacityException;
import fr.honeygroup.mapper.SessionMapper;
import fr.honeygroup.repository.PrestationRepository;
import fr.honeygroup.repository.SessionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests des règles métier de SessionService (BLL)")
class SessionServiceImplTest {

    @Mock private SessionRepository sessionRepository;
    @Mock private PrestationRepository prestationRepository;
    @Mock private SessionMapper sessionMapper;

    @InjectMocks
    private SessionServiceImpl sessionService;

    private Prestation prestationMock;
    private Session sessionMock;
    private SessionRequest requestValide;

    @BeforeEach
    void setUp() {
        prestationMock = new Prestation();
        prestationMock.setId(200L);

        sessionMock = new Session();
        sessionMock.setId(1L);
        sessionMock.setPrestation(prestationMock);
        sessionMock.setDateDebut(LocalDateTime.now().plusDays(5));
        sessionMock.setDateFin(LocalDateTime.now().plusDays(12));
        sessionMock.setCapaciteMax(15);
        sessionMock.setNbInscrits(5);
        sessionMock.setStatutSession(StatutSession.OUVERT);

        requestValide = new SessionRequest();
        requestValide.setPrestationId(200L);
        requestValide.setDateDebut(LocalDateTime.now().plusDays(5));
        requestValide.setDateFin(LocalDateTime.now().plusDays(12));
        requestValide.setCapaciteMax(20);
        requestValide.setStatut(StatutSession.OUVERT);
    }

    // ============================================================================
    // WORKFLOW DE CRÉATION (createSession)
    // ============================================================================

    @Test
    @DisplayName("Création Session : Succès nominal avec des dates futures valides")
    void createSession_Succes() {
        // Arrange
        when(prestationRepository.findById(200L)).thenReturn(Optional.of(prestationMock));
        when(sessionMapper.toEntity(requestValide)).thenReturn(sessionMock);
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sessionMapper.toResponse(any(Session.class))).thenReturn(new SessionResponse());

        // Act
        SessionResponse response = sessionService.createSession(requestValide);

        // Assert
        assertNotNull(response);
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    @DisplayName("Création Session : Échec si la date de début est planifiée dans le passé")
    void createSession_DateDebutDansLePasse_LanceBusinessLogicException() {
        // Arrange
        requestValide.setDateDebut(LocalDateTime.now().minusDays(2));
        requestValide.setDateFin(LocalDateTime.now().plusDays(5));

        // Act & Assert
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> 
            sessionService.createSession(requestValide)
        );
        assertTrue(exception.getMessage().contains("date de départ dans le passé"));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Création Session : Échec si la prestation parente rattachée n'existe pas")
    void createSession_PrestationIntrouvable_LanceBusinessLogicException() {
        // Arrange
        when(prestationRepository.findById(200L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> 
            sessionService.createSession(requestValide)
        );
        assertTrue(exception.getMessage().contains("Prestation introuvable"));
    }

    // ============================================================================
    // WORKFLOW DE MODIFICATION (updateSession)
    // ============================================================================

    @Test
    @DisplayName("Mise à jour Session : Succès avec modification de la capacité et de la prestation")
    void updateSession_Succes() {
        // Arrange
        Prestation nouvellePrestation = new Prestation();
        nouvellePrestation.setId(300L);

        requestValide.setPrestationId(300L);
        requestValide.setCapaciteMax(10); // Supérieur aux 5 inscrits actuels

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(sessionMock));
        when(prestationRepository.findById(300L)).thenReturn(Optional.of(nouvellePrestation));
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sessionMapper.toResponse(any(Session.class))).thenReturn(new SessionResponse());

        // Act
        SessionResponse response = sessionService.updateSession(1L, requestValide);

        // Assert
        assertNotNull(response);
        assertEquals(300L, sessionMock.getPrestation().getId());
        assertEquals(10, sessionMock.getCapaciteMax());
        verify(sessionRepository, times(1)).save(sessionMock);
    }

    @Test
    @DisplayName("Mise à jour Session : Échec si la nouvelle capacité maximale est inférieure au nombre actuel d'inscrits")
    void updateSession_CapaciteInsuffisante_LanceSessionCapacityException() {
        // Arrange
        sessionMock.setNbInscrits(12);
        requestValide.setCapaciteMax(10); // Moins que 12

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(sessionMock));

        // Act & Assert
        SessionCapacityException exception = assertThrows(SessionCapacityException.class, () -> 
            sessionService.updateSession(1L, requestValide)
        );
        assertTrue(exception.getMessage().contains("ne peut pas être inférieure au nombre de participants"));
        verify(sessionRepository, never()).save(any());
    }

    // ============================================================================
    // AUTOMATE DE STATUTS (transitionnerStatut)
    // ============================================================================

    @Test
    @DisplayName("Automate Statuts : Succès de la transition si autorisée par le workflow")
    void transitionnerStatut_Succes() {
        // Arrange
        // On mock indirectement le comportement de l'enum (ici simulé via Mockito ou l'objet réel)
        // Supposons que OUVERT -> COMPLET est une transition valide dans ton modèle
        StatutSession statutCible = StatutSession.COMPLET; 
        
        // Pour s'assurer que peutBasculerVers renvoie true durant le test si l'automate est strict
        // Si StatutSession est une enum réelle avec sa logique interne, on prend des statuts compatibles.
        // On simule une session existante
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(sessionMock));
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        String ancienStatut = sessionService.transitionnerStatut(1L, statutCible);

        // Assert
        assertEquals("OUVERT", ancienStatut);
        assertEquals(statutCible, sessionMock.getStatutSession());
        verify(sessionRepository, times(1)).save(sessionMock);
    }

    @Test
    @DisplayName("Automate Statuts : Échec et levée de BusinessSecurityException si la transition est illégale")
    void transitionnerStatut_TransitionIllegale_LanceBusinessSecurityException() {
        // Arrange
        // On injecte un statut cible que ton enum refusera (ex: forcer un statut incohérent)
        // Si l'automate refuse la transition, peutBasculerVers renverra false.
        // Pour le test, on va utiliser un mock espion ou s'appuyer sur la vraie enum défaillante.
        
        StatutSession statutInterdit = StatutSession.CLOTURE;
        
        // Si ton enum n'autorise pas OUVERT -> CLOTURE, le test passera tout seul ici :
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(sessionMock));

        // Act & Assert
        assertThrows(BusinessSecurityException.class, () -> 
            sessionService.transitionnerStatut(1L, statutInterdit)
        );
        verify(sessionRepository, never()).save(any());
    }

    // ============================================================================
    // VALIDATIONS SÉMANTIQUES (Routines de dates)
    // ============================================================================

    @Test
    @DisplayName("Chronologie Dates : Exception si une des dates obligatoires est manquante")
    void validerChronologieDates_DatesNulles_LanceBusinessLogicException() {
        // Cas Début Null
        requestValide.setDateDebut(null);
        assertThrows(BusinessLogicException.class, () -> sessionService.createSession(requestValide));

        // Cas Fin Null
        requestValide.setDateDebut(LocalDateTime.now().plusDays(2));
        requestValide.setDateFin(null);
        assertThrows(BusinessLogicException.class, () -> sessionService.createSession(requestValide));
    }

    @Test
    @DisplayName("Chronologie Dates : Exception si la date de fin précède la date de début")
    void validerChronologieDates_FinAvantDebut_LanceBusinessLogicException() {
        // Arrange
        requestValide.setDateDebut(LocalDateTime.now().plusDays(10));
        requestValide.setDateFin(LocalDateTime.now().plusDays(5)); // Inversion

        // Act & Assert
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> 
            sessionService.createSession(requestValide)
        );
        assertTrue(exception.getMessage().contains("La date de fin de la session doit être postérieure"));
    }

    @Test
    @DisplayName("Consultation : Extraction des détails d'une session par ID")
    void getSessionDetails_Combinaisons() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(sessionMock));
        when(sessionMapper.toResponse(sessionMock)).thenReturn(new SessionResponse());

        assertNotNull(sessionService.getSessionDetails(1L));

        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(BusinessLogicException.class, () -> sessionService.getSessionDetails(99L));
    }
}