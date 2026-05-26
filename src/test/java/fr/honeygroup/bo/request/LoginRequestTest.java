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

@DisplayName("Tests de validation du DTO LoginRequest")
class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès avec des identifiants valides")
    void loginRequest_Valide_Succes() {
        LoginRequest request = LoginRequest.builder()
                .email("user@honeygroup.fr")
                .password("Password123!")
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec si l'email est mal formé ou si le mot de passe est absent")
    void loginRequest_Invalide_Echec() {
        // Test Email invalide et mot de passe vide
        LoginRequest request = LoginRequest.builder()
                .email("email-invalide")
                .password("")
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Le DTO invalide devrait retourner des erreurs");
        assertEquals(2, violations.size(), "Deux violations étaient attendues");
    }

    @Test
    @DisplayName("Lombok : Vérification du pattern Builder")
    void loginRequest_Builder_Fonctionnel() {
        LoginRequest request = LoginRequest.builder()
                .email("contact@honeygroup.fr")
                .password("securePass")
                .build();

        assertEquals("contact@honeygroup.fr", request.getEmail());
        assertEquals("securePass", request.getPassword());
    }
}