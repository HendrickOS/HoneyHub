package fr.honeygroup.bo.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.honeygroup.enumeration.StatutPayment;
import fr.honeygroup.enumeration.TypePayment;

@DisplayName("Tests de structure du DTO PaymentResponse")
class PaymentResponseTest {

    @Test
    @DisplayName("Lombok : Vérification du Builder et de l'encapsulation")
    void paymentResponse_BuilderEtGetters_Fonctionnels() {
        BigDecimal montant = new BigDecimal("1250.00");
        LocalDateTime maintenant = LocalDateTime.now();
        
        PaymentResponse response = PaymentResponse.builder()
                .id(501L)
                .methode(TypePayment.VIREMENT_BANCAIRE)
                .transactionId("TRX-BANK-001")
                .montantPaye(montant)
                .datePaiement(maintenant)
                .statutPaiement(StatutPayment.EN_ATTENTE_PREUVE)
                .preuveUrl("/cdn/receipts/proof_501.jpg")
                .build();

        assertNotNull(response);
        assertEquals(501L, response.getId());
        assertEquals(TypePayment.VIREMENT_BANCAIRE, response.getMethode());
        assertEquals(montant, response.getMontantPaye());
        assertEquals(StatutPayment.EN_ATTENTE_PREUVE, response.getStatutPaiement());
        assertEquals("/cdn/receipts/proof_501.jpg", response.getPreuveUrl());
    }

    @Test
    @DisplayName("Consistance : Vérification du constructeur vide")
    void paymentResponse_ConstructeurVide_Fonctionnel() {
        PaymentResponse response = new PaymentResponse();
        response.setStatutPaiement(StatutPayment.VALIDE);
        assertEquals(StatutPayment.VALIDE, response.getStatutPaiement());
    }
}