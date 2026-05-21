package enumeration;

/**
 * Énumération modélisant les états successifs du cycle de vie d'une réservation (Workflow Booking)
 * au sein du pôle Écotourisme de Honey Group.
 * <p>
 * Ces constantes dictent la logique métier applicable dans la couche service, notamment l'impact 
 * sur le calcul des jauges de sessions et l'affichage de l'historique sur les interfaces applicatives.
 * </p>
 */
public enum StatutBooking {
    
    /**
     * État initial obligatoire de toute nouvelle réservation.
     * Le dossier est bloqué temporairement dans l'attente du téléversement de la preuve de transaction 
     * (capture d'écran, reçu PDF de virement bancaire ou Mobile Money) par le client.
     */
    EN_ATTENTE_PAIEMENT {
        @Override
        public boolean peutBasculerVers(StatutBooking nouveauStatut) {
            return nouveauStatut == CONFIRME || nouveauStatut == REFUSE;
        }
    },

    /**
     * Réservation validée et close positivement.
     * Cet état fait suite à la vérification humaine et comptable du justificatif de paiement par le gérant.
     * Les places associées sont définitivement verrouillées au sein de la session.
     */
    CONFIRME {
        @Override
        public boolean peutBasculerVers(StatutBooking nouveauStatut) {
            return nouveauStatut == DEMANDE_ANNULATION || nouveauStatut == ANNULE;
        }
    },

    /**
     * Phase transitoire de résiliation initiée par le client depuis son espace personnel.
     * Le dossier est placé en attente d'arbitrage et d'examen par l'équipe administrative de Honey Group.
     */
    DEMANDE_ANNULATION {
        @Override
        public boolean peutBasculerVers(StatutBooking nouveauStatut) {
            return nouveauStatut == ANNULE || nouveauStatut == CONFIRME;
        }
    },

    /**
     * Clôture définitive du dossier après approbation de la demande de rétractation par un gérant.
     * Ce statut déclenche automatiquement la libération mathématique des places occupées dans la jauge de la session.
     */
    ANNULE {
        @Override
        public boolean peutBasculerVers(StatutBooking nouveauStatut) {
            return false;
        }
    },

    /**
     * Fin de cycle faisant suite au rejet du dossier par l'administration (ex: justificatif de virement 
     * frauduleux, expiré ou montant perçu incomplet).
     */
    REFUSE {
        @Override
        public boolean peutBasculerVers(StatutBooking nouveauStatut) {
            return false;
        }
    };

    /**
     * Valide de manière défensive si la transition vers un nouvel état est légale
     * selon les règles du workflow Honey Group.
     * * @param nouveauStatut Le statut cible vers lequel le dossier tente de migrer.
     * @return true si le changement d'état respecte le workflow, sinon false.
     */
    public abstract boolean peutBasculerVers(StatutBooking nouveauStatut);
}