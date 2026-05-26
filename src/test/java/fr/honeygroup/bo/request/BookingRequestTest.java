package fr.honeygroup.bo.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.honeygroup.enumeration.TypeReservation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de validation du DTO BookingRequest")
class BookingRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation : Succès si tous les champs requis sont présents")
    void bookingRequest_Valide_Succes() {
        BookingRequest request = BookingRequest.builder()
                .sessionId(10L)
                .nbPersonnes(2)
                .typeReservation(TypeReservation.SESSION)
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Le DTO devrait être valide");
    }

    @Test
    @DisplayName("Validation : Échec si sessionId est nul ou nbPersonnes inférieur à 1")
    void bookingRequest_Invalide_Echec() {
        BookingRequest request = BookingRequest.builder()
                .sessionId(null) // Violation
                .nbPersonnes(0)  // Violation
                .typeReservation(null) // Violation
                .build();

        var violations = validator.validate(request);
        assertEquals(3, violations.size(), "Il devrait y avoir 3 violations");
    }

    @Test
    @DisplayName("Lombok : Vérification des accès via getters/setters")
    void bookingRequest_Lombok_Fonctionnel() {
        BookingRequest request = new BookingRequest();
        request.setSessionId(50L);
        request.setNbPersonnes(1);
        request.setTypeReservation(TypeReservation.SUR_MESURE);

        assertEquals(50L, request.getSessionId());
        assertEquals(1, request.getNbPersonnes());
        assertEquals(TypeReservation.SUR_MESURE, request.getTypeReservation());
    }
}