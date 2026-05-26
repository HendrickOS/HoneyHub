package fr.honeygroup.bo.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de validation du DTO ResetPasswordRequest")
class ResetPasswordRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès avec un token et un mot de passe conforme")
    void resetPasswordRequest_Valide_Succes() {
        ResetPasswordRequest request = new ResetPasswordRequest("token-uuid-12345", "NouveauPass123!");

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec si le token est vide ou le mot de passe trop court")
    void resetPasswordRequest_Invalide_Echec() {
        // Test token vide et mot de passe court
        ResetPasswordRequest request = new ResetPasswordRequest("", "court");

        var violations = validator.validate(request);
        assertEquals(2, violations.size(), "Deux violations étaient attendues");
        
        // Vérification des messages d'erreur
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Le token est requis")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("au moins 8 caracteres")));
    }

    @Test
    @DisplayName("Lombok : Vérification des accès via getters/setters")
    void resetPasswordRequest_Lombok_Fonctionnel() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("uuid-token");
        request.setNewPassword("Secure123456");
        
        assertEquals("uuid-token", request.getToken());
        assertEquals("Secure123456", request.getNewPassword());
    }
}