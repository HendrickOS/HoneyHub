package fr.honeygroup.bo.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.honeygroup.enumeration.StatutSession;

@DisplayName("Tests de structure du DTO SessionResponse")
class SessionResponseTest { // Renommé de TokenResponseTest en SessionResponseTest

    @Test
    @DisplayName("Lombok : Vérification du Builder et de l'aplatissement (flattening)")
    void sessionResponse_BuilderEtGetters_Fonctionnels() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(5);

        SessionResponse response = SessionResponse.builder()
                .id(1L)
                .prestationId(101L)
                .prestationNom("Trek Sahara") // Donnée aplatie pour le Front-end
                .dateDebut(start)
                .dateFin(end)
                .statut(StatutSession.OUVERT)
                .capaciteMax(15)
                .participantsActuels(4)
                .build();

        assertNotNull(response);
        assertEquals("Trek Sahara", response.getPrestationNom());
        assertEquals(15, response.getCapaciteMax());
        assertEquals(4, response.getParticipantsActuels());
        assertEquals(StatutSession.OUVERT, response.getStatut());
    }

    @Test
    @DisplayName("Consistance : Vérification du constructeur vide")
    void sessionResponse_ConstructeurVide_Fonctionnel() {
        SessionResponse response = new SessionResponse();
        response.setId(2L);
        assertEquals(2L, response.getId());
    }
}