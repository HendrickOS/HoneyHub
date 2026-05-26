package fr.honeygroup.bo.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de structure du DTO UserProfileResponse")
class UserProfileResponseTest {

    @Test
    @DisplayName("Lombok : Vérification du Builder et de l'agrégation des données")
    void userProfileResponse_Builder_Fonctionnel() {
        UserProfileResponse response = UserProfileResponse.builder()
                .id(1L)
                .email("jean.dupont@example.com")
                .nom("Dupont")
                .prenom("Jean")
                .role("CLIENT")
                .adresse("12 rue de la Paix, 75000 Paris")
                .telephone("+33601020304")
                .pays("France")
                .preferences("Besoin d'un accès PMR")
                .build();

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("jean.dupont@example.com", response.getEmail());
        assertEquals("Dupont", response.getNom());
        assertEquals("CLIENT", response.getRole());
        assertEquals("+33601020304", response.getTelephone());
        assertEquals("Besoin d'un accès PMR", response.getPreferences());
    }

    @Test
    @DisplayName("Consistance : Vérification du constructeur vide")
    void userProfileResponse_ConstructeurVide_Fonctionnel() {
        UserProfileResponse response = new UserProfileResponse();
        response.setEmail("test@honeygroup.fr");
        assertEquals("test@honeygroup.fr", response.getEmail());
    }
}