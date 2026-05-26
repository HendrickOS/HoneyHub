package fr.honeygroup.enumeration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests du Workflow de paiement (StatutPayment)")
class StatutPaymentTest {

    @Test
    @DisplayName("Workflow : Transitions autorisées depuis l'état initial EN_ATTENTE_PREUVE")
    void transitions_DepuisEnAttentePreuve() {
        assertTrue(StatutPayment.EN_ATTENTE_PREUVE.peutBasculerVers(StatutPayment.EN_VERIFICATION), 
                "La soumission de la preuve par le client doit faire passer le paiement à EN_VERIFICATION.");
        
        assertFalse(StatutPayment.EN_ATTENTE_PREUVE.peutBasculerVers(StatutPayment.VALIDE), 
                "Interdiction de valider un paiement tant que la preuve n'a pas été fournie.");
        assertFalse(StatutPayment.EN_ATTENTE_PREUVE.peutBasculerVers(StatutPayment.REJETE), 
                "Interdiction de rejeter un paiement sans pièce justificative reçue.");
    }

    @Test
    @DisplayName("Workflow : Transitions autorisées depuis EN_VERIFICATION")
    void transitions_DepuisEnVerification() {
        assertTrue(StatutPayment.EN_VERIFICATION.peutBasculerVers(StatutPayment.VALIDE), 
                "Le gérant doit pouvoir valider un paiement en vérification.");
        assertTrue(StatutPayment.EN_VERIFICATION.peutBasculerVers(StatutPayment.REJETE), 
                "Le gérant doit pouvoir rejeter un paiement non conforme.");
        
        assertFalse(StatutPayment.EN_VERIFICATION.peutBasculerVers(StatutPayment.EN_ATTENTE_PREUVE), 
                "Un dossier en cours d'analyse comptable ne peut pas rétrograder à l'état initial.");
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
        
        // Sécurité d'exhaustivité sur l'immuabilité (boucle défensive)
        for (StatutPayment statut : StatutPayment.values()) {
            assertFalse(StatutPayment.VALIDE.peutBasculerVers(statut), "Aucune transition sortante permise depuis VALIDE.");
            assertFalse(StatutPayment.REJETE.peutBasculerVers(statut), "Aucune transition sortante permise depuis REJETE.");
        }
    }

    @Test
    @DisplayName("Méthode verifierTransition() : Trajectoire nominale et blocage par exception")
    void verifierTransition_ComportementDefensif() {
        // 1. Cas passant : pas d'exception
        assertDoesNotThrow(() -> 
            StatutPayment.EN_ATTENTE_PREUVE.verifierTransition(StatutPayment.EN_VERIFICATION),
            "La transition réglementaire ne doit soulever aucune exception."
        );

        // 2. Cas d'échec : levée d'une IllegalStateException
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
            StatutPayment.VALIDE.verifierTransition(StatutPayment.REJETE)
        );

        // 3. Validation du message de log
        assertEquals("Transition illégale de VALIDE vers REJETE", exception.getMessage(),
                "Le message d'erreur de l'exception doit correspondre exactement au format du workflow financier.");
    }
}