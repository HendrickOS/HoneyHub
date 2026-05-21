package fr.honeygroup.bo;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import enumeration.StatutPayment;

@DisplayName("Tests unitaires de l'entité Payment (Flux Financiers)")
class PaymentTest {

    private Booking bookingDeTest;

    @BeforeEach
    void setUp() {
        // Initialisation de la réservation requise pour lier un paiement
        bookingDeTest = new Booking();
    }

    @Test
    @DisplayName("Vérification des valeurs initiales et des états par défaut d'un Paiement")
    void initialisation_DevraitAvoirLesValeursParDefaut() {
        // ARRANGE & ACT
        Payment payment = new Payment();

        // ASSERT
        assertEquals(StatutPayment.EN_VERIFICATION, payment.getStatutPaiement(), 
                "Le statut initial doit obligatoirement être EN_VERIFICATION.");
        assertNotNull(payment.getDatePaiement(), 
                "La date de paiement doit être initialisée automatiquement dès l'instanciation.");
        assertTrue(payment.getDatePaiement().isBefore(LocalDateTime.now().plusSeconds(1)), 
                "La date d'initialisation doit correspondre à l'heure actuelle.");
    }

    @Test
    @DisplayName("Vérification du fonctionnement du Builder Lombok sur l'ensemble des champs du flux financier")
    void builder_DevraitConstruireLObjetCorrectement() {
        // ARRANGE & ACT
        Payment payment = Payment.builder()
                .id(42L)
                .booking(bookingDeTest)
                .methode("VIREMENT_BANCAIRE")
                .transactionId("TX-HONEY-998877")
                .montantPaye(new BigDecimal("1500.00"))
                .preuveUrl("uploads/justificatifs/recu_virement_42.pdf")
                .statutPaiement(StatutPayment.VALIDE)
                .build();

        // ASSERT
        assertNotNull(payment);
        assertEquals(42L, payment.getId());
        assertEquals(bookingDeTest, payment.getBooking());
        assertEquals("VIREMENT_BANCAIRE", payment.getMethode());
        assertEquals("TX-HONEY-998877", payment.getTransactionId());
        assertEquals(new BigDecimal("1500.00"), payment.getMontantPaye());
        assertEquals("uploads/justificatifs/recu_virement_42.pdf", payment.getPreuveUrl());
        assertEquals(StatutPayment.VALIDE, payment.getStatutPaiement());
    }
}