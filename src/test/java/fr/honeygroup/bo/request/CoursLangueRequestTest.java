package fr.honeygroup.bo.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de validation du DTO CoursLangueRequest")
class CoursLangueRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès si tous les champs requis sont conformes")
    void coursLangueRequest_Valide_Succes() {
        CoursLangueRequest request = CoursLangueRequest.builder()
                .titreService("Anglais Business") // Hérité
                .langue("Anglais")
                .niveau("B2")
                .descriptifProgramme("Programme intensif axé sur les réunions professionnelles (20+ chars).")
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec si les contraintes de taille ou présence ne sont pas respectées")
    void coursLangueRequest_Invalide_Echec() {
        CoursLangueRequest request = CoursLangueRequest.builder()
                .langue("")                 // Violation : @NotBlank
                .niveau("Niveau trop long".repeat(10)) // Violation : > 50 chars
                .descriptifProgramme("Trop court")     // Violation : < 20 chars
                .build();

        var violations = validator.validate(request);
        assertEquals(3, violations.size(), "Il devrait y avoir 3 violations de contrainte");
    }

    @Test
    @DisplayName("Héritage : Vérification du SuperBuilder avec champs parents et enfants")
    void coursLangueRequest_SuperBuilder_Fonctionnel() {
        CoursLangueRequest request = CoursLangueRequest.builder()
                .titreService("Japonais")
                .langue("Japonais")
                .niveau("N4")
                .descriptifProgramme("Apprentissage des kanjis de base pour débutants absolus.")
                .build();

        assertEquals("Japonais", request.getTitreService());
        assertEquals("N4", request.getNiveau());
    }
}