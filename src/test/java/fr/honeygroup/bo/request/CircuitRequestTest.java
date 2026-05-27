package fr.honeygroup.bo.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de validation du DTO CircuitRequest")
class CircuitRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès si tous les champs requis sont conformes")
    void circuitRequest_Valide_Succes() {
        CircuitRequest request = CircuitRequest.builder()
                .poleId(1L)
                .titreService("Safari Écotouristique") // Hérité
                .description("Une description de prestation valide et suffisamment longue.")
                .prixBase(250.0)
                .descriptionLongue("Une aventure immersive de 20 caractères minimum au cœur du parc national.")
                .itineraire("Antananarivo -> Parc A -> Parc B")
                .duree("7 jours / 6 nuits")
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec si les champs obligatoires sont vides ou trop courts")
    void circuitRequest_Invalide_Echec() {
        CircuitRequest request = CircuitRequest.builder()
                .poleId(1L)
                .titreService("Safari")
                .description("Une description de prestation valide et suffisamment longue.")
                .prixBase(250.0)
                .descriptionLongue("Trop court") // Violation : < 20
                .itineraire("")                   // Violation : @NotBlank
                .duree(null)                      // Violation : @NotBlank
                .build();

        var violations = validator.validate(request);
        assertEquals(3, violations.size(), "Il devrait y avoir 3 violations");
    }

    @Test
    @DisplayName("Héritage : Vérification du fonctionnement du SuperBuilder")
    void circuitRequest_SuperBuilder_Fonctionnel() {
        CircuitRequest request = CircuitRequest.builder()
                .titreService("Titre parent")
                .duree("2 jours")
                .build();

        assertEquals("Titre parent", request.getTitreService());
        assertEquals("2 jours", request.getDuree());
    }
}