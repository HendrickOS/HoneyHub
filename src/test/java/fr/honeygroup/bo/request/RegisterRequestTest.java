package fr.honeygroup.bo.request;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.honeygroup.enumeration.Role;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de validation du DTO RegisterRequest")
class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès avec des données conformes")
    void registerRequest_Valide_Succes() {
        RegisterRequest request = RegisterRequest.builder()
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@honeygroup.fr")
                .telephone("+33612345678")
                .password("Password123!")
                .adresse("12 rue de la Paix, 75002 Paris")
                .pays("France")
                .role(Role.CLIENT)
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec avec données invalides (Regex et Tailles)")
    void registerRequest_Invalide_Echec() {
        RegisterRequest request = RegisterRequest.builder()
                .nom("J1")               // Violation : pattern non respecté (chiffre)
                .email("email-invalide") // Violation : @Email
                .telephone("06123")      // Violation : format international
                .password("short")       // Violation : < 8
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 4);
    }
}