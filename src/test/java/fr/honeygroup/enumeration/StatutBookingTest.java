package fr.honeygroup.enumeration;

import static org.junit.jupiter.api.Assertions.*;

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
    }

    @Test
    @DisplayName("Workflow : Transitions autorisées depuis l'état CONFIRME")
    void transitions_DepuisConfirme() {
        assertTrue(StatutBooking.CONFIRME.peutBasculerVers(StatutBooking.DEMANDE_ANNULATION), 
                "Un client doit pouvoir initier une demande de rétractation.");
        assertFalse(StatutBooking.CONFIRME.peutBasculerVers(StatutBooking.REFUSE), 
                "Un dossier confirmé ne peut plus être refusé d'office sans arbitrage.");
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
}