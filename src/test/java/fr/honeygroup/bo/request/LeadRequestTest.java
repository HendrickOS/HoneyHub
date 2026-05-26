package fr.honeygroup.bo.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de validation du DTO LeadRequest")
class LeadRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès avec données minimales et Map remplie")
    void leadRequest_Valide_Succes() {
        LeadRequest request = new LeadRequest();
        request.setNom("Jean Dupont");
        request.setEmail("jean@example.com");
        request.setPoleId(1L);
        request.setSource("Google");
        request.setSpecificDetails(Map.of("Besoin", "Devis IT"));

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec si champs obligatoires absents")
    void leadRequest_Invalide_Echec() {
        LeadRequest request = new LeadRequest();
        // poleId, source et specificDetails sont obligatoires
        request.setNom("J"); // Trop court (min 2)
        request.setEmail("email-invalide"); // Violation @Email

        var violations = validator.validate(request);
        
        // poleId manque (1), source manque (2), specificDetails manque (3), nom trop court (4), email invalide (5)
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 3);
    }

    @Test
    @DisplayName("Structure : Vérification de l'intégrité de la Map spécifique")
    void leadRequest_SpecificDetails_Fonctionnel() {
        LeadRequest request = new LeadRequest();
        Map<String, String> details = Map.of("Budget", "10k", "Urgence", "Haute");
        request.setSpecificDetails(details);

        assertEquals(2, request.getSpecificDetails().size());
        assertEquals("10k", request.getSpecificDetails().get("Budget"));
    }
}