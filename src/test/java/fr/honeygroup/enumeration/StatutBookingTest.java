package fr.honeygroup.enumeration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests du Workflow de cycle de vie (StatutBooking)")
class StatutBookingTest {

    @Test
    @DisplayName("Workflow : Transitions autorisées depuis l'état initial EN_ATTENTE_PAIEMENT")
    void transitions_DepuisEnAttentePaiement() {
        assertTrue(StatutBooking.EN_ATTENTE_PAIEMENT.peutBasculerVers(StatutBooking.CONFIRME), 
                "Un paiement valide doit faire passer le dossier à CONFIRME.");
        assertTrue(StatutBooking.EN_ATTENTE_PAIEMENT.peutBasculerVers(StatutBooking.REFUSE), 
                "Un justificatif frauduleux doit faire passer le dossier à REFUSE.");
        
        assertFalse(StatutBooking.EN_ATTENTE_PAIEMENT.peutBasculerVers(StatutBooking.DEMANDE_ANNULATION), 
                "Impossible de demander une annulation administrative sans avoir payé.");
        assertFalse(StatutBooking.EN_ATTENTE_PAIEMENT.peutBasculerVers(StatutBooking.ANNULE), 
                "Impossible d'annuler directement sans passer par une confirmation ou un refus.");
    }

    @Test
    @DisplayName("Workflow : Transitions autorisées depuis l'état CONFIRME")
    void transitions_DepuisConfirme() {
        assertTrue(StatutBooking.CONFIRME.peutBasculerVers(StatutBooking.DEMANDE_ANNULATION), 
                "Un client doit pouvoir initier une demande de rétractation.");
        assertTrue(StatutBooking.CONFIRME.peutBasculerVers(StatutBooking.ANNULE), 
                "Un gérant doit pouvoir annuler directement un dossier validé en cas de force majeure.");
        
        assertFalse(StatutBooking.CONFIRME.peutBasculerVers(StatutBooking.REFUSE), 
                "Un dossier confirmé ne peut plus être refusé d'office sans arbitrage.");
    }

    @Test
    @DisplayName("Workflow : Transitions autorisées depuis l'état DEMANDE_ANNULATION")
    void transitions_DepuisDemandeAnnulation() {
        assertTrue(StatutBooking.DEMANDE_ANNULATION.peutBasculerVers(StatutBooking.ANNULE), 
                "L'administration doit pouvoir accepter la rétractation et annuler le dossier.");
        assertTrue(StatutBooking.DEMANDE_ANNULATION.peutBasculerVers(StatutBooking.CONFIRME), 
                "L'administration doit pouvoir rejeter la rétractation et rétablir le dossier à CONFIRME.");
        
        assertFalse(StatutBooking.DEMANDE_ANNULATION.peutBasculerVers(StatutBooking.REFUSE), 
                "Une demande d'annulation ne peut pas muter vers un refus de paiement.");
    }

    @Test
    @DisplayName("Workflow : Immuabilité des états terminaux (ANNULE et REFUSE)")
    void transitions_EtatsTerminaux() {
        // Test pour le statut ANNULE
        for (StatutBooking statut : StatutBooking.values()) {
            assertFalse(StatutBooking.ANNULE.peutBasculerVers(statut), 
                    "Aucune transition ne doit être permise depuis l'état final ANNULE.");
        }

        // Test pour le statut REFUSE
        for (StatutBooking statut : StatutBooking.values()) {
            assertFalse(StatutBooking.REFUSE.peutBasculerVers(statut), 
                    "Aucune transition ne doit être permise depuis l'état final REFUSE.");
        }
    }

    @Test
    @DisplayName("Méthode verifierTransition() : Trajectoire nominale autorisée")
    void verifierTransition_CasAutorise() {
        // Ne doit lever aucune exception
        assertDoesNotThrow(() -> 
            StatutBooking.EN_ATTENTE_PAIEMENT.verifierTransition(StatutBooking.CONFIRME)
        );
    }

    @Test
    @DisplayName("Méthode verifierTransition() : Blocage défensif avec exception attendue")
    void verifierTransition_CasInterdit() {
        // Doit lever une IllegalStateException
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
            StatutBooking.REFUSE.verifierTransition(StatutBooking.CONFIRME)
        );

        // Validation du message d'erreur pour garantir une bonne restitution dans les logs
        String messageAttendu = "Transition illégale de REFUSE vers CONFIRME";
        assertEquals(messageAttendu, exception.getMessage(), "Le message de l'exception doit expliciter l'erreur de workflow.");
    }
}