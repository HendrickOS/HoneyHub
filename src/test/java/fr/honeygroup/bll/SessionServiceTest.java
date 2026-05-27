package fr.honeygroup.bll;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.honeygroup.bll.impl.SessionServiceImpl;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.request.SessionRequest;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessLogicException;
import fr.honeygroup.repository.SessionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service SessionService")
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    @DisplayName("Mise à jour : Empêcher réduction de capacité sous le nombre d'inscrits")
    void updateSession_ShouldThrowException_WhenCapacityLowerThanInscrits() {
        // 1. Préparation
        Long sessionId = 1L;
        Session existingSession = new Session();
        existingSession.setId(sessionId);
        existingSession.setNbInscrits(5);
        existingSession.setCapaciteMax(10);
        
        SessionRequest request = new SessionRequest();
        request.setCapaciteMax(4); // Tentative de passer en dessous des inscrits (5)
        
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession));

        // 2. Exécution & Vérification
        assertThatThrownBy(() -> sessionService.updateSession(sessionId, request))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessageContaining("capacité maximale");
    }
}