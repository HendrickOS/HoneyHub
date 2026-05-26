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

@DisplayName("Tests de validation du DTO ForgotPasswordRequest")
class ForgotPasswordRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès avec un format email valide")
    void forgotPasswordRequest_Valide_Succes() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("client@honeygroup.fr");

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "L'email devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec si l'email est vide ou mal formé")
    void forgotPasswordRequest_Invalide_Echec() {
        // Test Email vide
        ForgotPasswordRequest emptyRequest = new ForgotPasswordRequest("");
        assertFalse(validator.validate(emptyRequest).isEmpty(), "L'email vide devrait échouer");

        // Test Email mal formé
        ForgotPasswordRequest malformedRequest = new ForgotPasswordRequest("format-invalide-email");
        var violations = validator.validate(malformedRequest);
        
        assertFalse(violations.isEmpty(), "Le format invalide devrait échouer");
        assertEquals("Format d'email invalide", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Lombok : Vérification des accès via getters/setters")
    void forgotPasswordRequest_Lombok_Fonctionnel() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@honeygroup.fr");
        
        assertEquals("test@honeygroup.fr", request.getEmail());
    }
}