package fr.honeygroup.bo.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.honeygroup.enumeration.TypePayment;

@DisplayName("Tests de structure du DTO PaymentRequest")
class PaymentRequestTest {

    @Test
    @DisplayName("Lombok : Vérification du Builder et de l'encapsulation")
    void paymentRequest_BuilderEtGetters_Fonctionnels() {
        BigDecimal montant = new BigDecimal("1500.50");
        
        PaymentRequest request = PaymentRequest.builder()
                .bookingId(101L)
                .montantPaye(montant)
                .transactionId("TXN-998877")
                .methode(TypePayment.MOBILE_MONEY)
                .preuveUrl("/storage/receipts/proof_101.pdf")
                .build();

        assertNotNull(request);
        assertEquals(101L, request.getBookingId());
        assertEquals(montant, request.getMontantPaye());
        assertEquals("TXN-998877", request.getTransactionId());
        assertEquals(TypePayment.MOBILE_MONEY, request.getMethode());
        assertEquals("/storage/receipts/proof_101.pdf", request.getPreuveUrl());
    }

    @Test
    @DisplayName("Consistance : Vérification du constructeur vide (NoArgsConstructor)")
    void paymentRequest_ConstructeurVide_Fonctionnel() {
        PaymentRequest request = new PaymentRequest();
        assertNull(request.getBookingId());
        
        request.setBookingId(202L);
        assertEquals(202L, request.getBookingId());
    }
}