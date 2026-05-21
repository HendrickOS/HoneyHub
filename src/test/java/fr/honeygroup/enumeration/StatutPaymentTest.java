package fr.honeygroup.enumeration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests du Workflow de paiement (StatutPayment)")
class StatutPaymentTest {

    @Test
    @DisplayName("Workflow : Transitions autorisées depuis EN_VERIFICATION")
    void transitions_DepuisEnVerification() {
        assertTrue(StatutPayment.EN_VERIFICATION.peutBasculerVers(StatutPayment.VALIDE), 
                "Le gérant doit pouvoir valider un paiement en vérification.");
        assertTrue(StatutPayment.EN_VERIFICATION.peutBasculerVers(StatutPayment.REJETE), 
                "Le gérant doit pouvoir rejeter un paiement non conforme.");
    }

    @Test
    @DisplayName("Workflow : Immuabilité après décision (VALIDE/REJETE)")
    void transitions_EtatsTerminaux() {
        // Un paiement validé ne peut plus bouger
        assertFalse(StatutPayment.VALIDE.peutBasculerVers(StatutPayment.EN_VERIFICATION), 
                "Un paiement déjà validé ne doit pas pouvoir revenir en vérification.");
        assertFalse(StatutPayment.VALIDE.peutBasculerVers(StatutPayment.REJETE), 
                "Un paiement validé ne doit pas pouvoir être rejeté.");

        // Un paiement rejeté ne peut plus bouger
        assertFalse(StatutPayment.REJETE.peutBasculerVers(StatutPayment.VALIDE), 
                "Un paiement rejeté ne doit pas pouvoir être validé sans nouvelle soumission.");
        assertFalse(StatutPayment.REJETE.peutBasculerVers(StatutPayment.EN_VERIFICATION), 
                "Un paiement rejeté ne doit pas pouvoir revenir en vérification sans nouvelle soumission.");
    }
}