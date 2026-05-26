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

@DisplayName("Tests de validation du DTO ProfileUpdateRequest")
class ProfileUpdateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès avec uniquement les champs optionnels manquants")
    void profileUpdateRequest_Partiel_Succes() {
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .nom("Dupont")
                .prenom("Jean")
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide même avec des champs optionnels absents");
    }

    @Test
    @DisplayName("Validation : Succès avec un numéro de téléphone au format E.164")
    void profileUpdateRequest_TelephoneValide_Succes() {
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .telephone("+33612345678")
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le format E.164 devrait être accepté");
    }

    @Test
    @DisplayName("Validation : Échec si le téléphone ne respecte pas le format international")
    void profileUpdateRequest_TelephoneInvalide_Echec() {
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .telephone("0612345678") // Manque le préfixe ou format incorrect pour E.164 sans +
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Le numéro devrait être invalide");
        assertEquals("Numéro de téléphone invalide (format international requis)", violations.iterator().next().getMessage());
    }
}