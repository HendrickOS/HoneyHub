package fr.honeygroup.bo.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de validation du DTO PoleRequest (i18n)")
class PoleRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès avec des données conformes aux bornes i18n")
    void poleRequest_Valide_Succes() {
        PoleRequest request = new PoleRequest();
        request.setNom("Écotourisme");
        request.setDescription("Pôle dédié à la promotion du voyage durable et solidaire.");

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec si les contraintes de taille i18n sont violées")
    void poleRequest_Invalide_Echec() {
        PoleRequest request = new PoleRequest();
        request.setNom("IT"); // Trop court (min 3)
        request.setDescription("Trop court"); // Trop court (min 10)

        var violations = validator.validate(request);
        assertEquals(2, violations.size(), "Il devrait y avoir 2 violations");
        
        // Vérification que les messages de clés i18n sont bien propagés
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{pole.nom.size}")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{pole.description.size}")));
    }
}