package fr.honeygroup.bo.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de validation du DTO RefreshTokenRequest")
class RefreshTokenRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès si le token est présent")
    void refreshTokenRequest_Valide_Succes() {
        RefreshTokenRequest request = new RefreshTokenRequest("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec si le token est vide ou nul")
    void refreshTokenRequest_Invalide_Echec() {
        // Test token vide
        RefreshTokenRequest emptyRequest = new RefreshTokenRequest("");
        assertFalse(validator.validate(emptyRequest).isEmpty(), "Un token vide devrait échouer");

        // Test token null (si instancié sans constructeur ou via setter)
        RefreshTokenRequest nullRequest = new RefreshTokenRequest();
        assertFalse(validator.validate(nullRequest).isEmpty(), "Un token nul devrait échouer");
    }

    @Test
    @DisplayName("Lombok : Vérification des accès via getters/setters")
    void refreshTokenRequest_Lombok_Fonctionnel() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("uuid-token-test");
        
        assertEquals("uuid-token-test", request.getRefreshToken());
    }
}