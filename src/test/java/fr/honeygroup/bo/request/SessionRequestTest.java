package fr.honeygroup.bo.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.honeygroup.enumeration.StatutSession;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de validation du DTO SessionRequest")
class SessionRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès avec des données de session cohérentes")
    void sessionRequest_Valide_Succes() {
        SessionRequest request = SessionRequest.builder()
                .prestationId(100L)
                .dateDebut(LocalDateTime.now().plusDays(7))
                .dateFin(LocalDateTime.now().plusDays(14))
                .capaciteMax(12)
                .statut(StatutSession.OUVERT)
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec si les champs obligatoires sont manquants ou invalides")
    void sessionRequest_Invalide_Echec() {
        SessionRequest request = SessionRequest.builder()
                .prestationId(null)   // Violation : @NotNull
                .capaciteMax(0)       // Violation : @Min(1)
                .build();

        var violations = validator.validate(request);
        assertEquals(3, violations.size(), "3 violations attendues (prestationId, dateDebut, dateFin nulles)");
    }

    @Test
    @DisplayName("Structure : Vérification de la construction via Builder")
    void sessionRequest_Builder_Fonctionnel() {
        LocalDateTime now = LocalDateTime.now();
        SessionRequest request = SessionRequest.builder()
                .prestationId(5L)
                .dateDebut(now)
                .dateFin(now.plusDays(3))
                .capaciteMax(10)
                .build();

        assertEquals(5L, request.getPrestationId());
        assertEquals(10, request.getCapaciteMax());
        assertNull(request.getStatut(), "Le statut devrait être null par défaut");
    }
}