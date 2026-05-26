package fr.honeygroup.bo.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de structure du DTO TokenResponse")
class TokenResponseTest {

    @Test
    @DisplayName("Lombok : Vérification du Builder et de l'encapsulation")
    void tokenResponse_BuilderEtGetters_Fonctionnels() {
        String access = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        String refresh = "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d";

        TokenResponse response = TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .build();

        assertNotNull(response);
        assertEquals(access, response.getAccessToken());
        assertEquals(refresh, response.getRefreshToken());
    }

    @Test
    @DisplayName("Consistance : Vérification du constructeur vide")
    void tokenResponse_ConstructeurVide_Fonctionnel() {
        TokenResponse response = new TokenResponse();
        response.setAccessToken("token-test");
        assertEquals("token-test", response.getAccessToken());
    }
}