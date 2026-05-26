package fr.honeygroup.bo.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.honeygroup.enumeration.StatutPrestation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de validation du DTO PrestationRequest (Socle)")
class PrestationRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès avec tous les champs requis")
    void prestationRequest_Valide_Succes() {
        PrestationRequest request = PrestationRequest.builder()
                .poleId(1L)
                .titreService("Trek Découverte")
                .description("Un superbe circuit dans les montagnes locales.")
                .prixBase(450.0)
                .statut(StatutPrestation.ACTIF)
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec si les contraintes de taille ou nullité ne sont pas respectées")
    void prestationRequest_Invalide_Echec() {
        PrestationRequest request = PrestationRequest.builder()
                .poleId(null)          // Violation : @NotNull
                .titreService("Tr")    // Violation : < 3
                .description("Court")  // Violation : < 10
                .prixBase(null)        // Violation : @NotNull
                .build();

        var violations = validator.validate(request);
        assertEquals(4, violations.size(), "Il devrait y avoir 4 violations");
    }

    @Test
    @DisplayName("Metadata : Vérification de l'initialisation par défaut via Builder")
    void prestationRequest_Metadata_Initialisation() {
        PrestationRequest request = PrestationRequest.builder()
                .poleId(1L)
                .titreService("Test")
                .description("Description valide")
                .prixBase(10.0)
                .build();

        assertNotNull(request.getMetadata(), "La Map metadata ne doit pas être nulle");
        assertTrue(request.getMetadata().isEmpty(), "La Map doit être initialisée vide par défaut");
        
        request.getMetadata().put("Key", "Value");
        assertEquals(1, request.getMetadata().size());
    }
}