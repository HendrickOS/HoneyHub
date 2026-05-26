package fr.honeygroup.bo.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de structure du DTO LeadResponse")
class LeadResponseTest {

    @Test
    @DisplayName("Lombok : Vérification du Builder et de l'encapsulation")
    void leadResponse_BuilderEtGetters_Fonctionnels() {
        Map<String, String> details = Map.of("Projet", "Développement Web", "Deadline", "2026-09");
        LocalDateTime now = LocalDateTime.now();

        LeadResponse response = LeadResponse.builder()
                .id(1L)
                .dateSoumission(now)
                .statut("NOUVEAU")
                .source("Instagram")
                .userNomComplet("Alice Martin")
                .poleNom("IT Outsourcing")
                .specificDetails(details)
                .build();

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Instagram", response.getSource());
        assertEquals("Alice Martin", response.getUserNomComplet());
        assertEquals("IT Outsourcing", response.getPoleNom());
        assertEquals(2, response.getSpecificDetails().size());
        assertEquals("Développement Web", response.getSpecificDetails().get("Projet"));
    }

    @Test
    @DisplayName("Consistance : Vérification du constructeur vide")
    void leadResponse_ConstructeurVide_Fonctionnel() {
        LeadResponse response = new LeadResponse();
        response.setId(99L);
        assertEquals(99L, response.getId());
    }
}