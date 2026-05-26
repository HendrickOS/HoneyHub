package fr.honeygroup.bo.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de structure du DTO PoleResponse")
class PoleResponseTest {

    @Test
    @DisplayName("Lombok : Vérification du Builder et de l'encapsulation")
    void poleResponse_BuilderEtGetters_Fonctionnels() {
        PoleResponse response = PoleResponse.builder()
                .id(1L)
                .nom("IT Outsourcing")
                .description("Expertise en développement et gestion de projets IT.")
                .build();

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("IT Outsourcing", response.getNom());
        assertEquals("Expertise en développement et gestion de projets IT.", response.getDescription());
    }

    @Test
    @DisplayName("Consistance : Vérification du constructeur vide")
    void poleResponse_ConstructeurVide_Fonctionnel() {
        PoleResponse response = new PoleResponse();
        response.setNom("Écotourisme");
        assertEquals("Écotourisme", response.getNom());
    }
}