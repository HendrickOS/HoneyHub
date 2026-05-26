package fr.honeygroup.bo.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.honeygroup.enumeration.TypeReservation;

@DisplayName("Tests de structure du DTO BookingResponse")
class BookingResponseTest {

    @Test
    @DisplayName("Lombok : Vérification du Builder et de l'encapsulation")
    void bookingResponse_BuilderEtGetters_Fonctionnels() {
        // Simulation d'une liste de paiements associée
        List<PaymentResponse> payments = new ArrayList<>();
        
        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .typeReservation(TypeReservation.SESSION)
                .userId(10L)
                .userNomComplet("Jean DUPONT")
                .prestationTitre("Trek Atlas")
                .nbPersonnes(2)
                .montantTotal(new BigDecimal("900.00"))
                .payments(payments)
                .build();

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Jean DUPONT", response.getUserNomComplet());
        assertEquals(2, response.getNbPersonnes());
        assertEquals(new BigDecimal("900.00"), response.getMontantTotal());
        assertEquals(payments, response.getPayments());
    }

    @Test
    @DisplayName("Consistance : Vérification du constructeur vide")
    void bookingResponse_ConstructeurVide_Fonctionnel() {
        BookingResponse response = new BookingResponse();
        response.setId(5L);
        assertEquals(5L, response.getId());
    }
}