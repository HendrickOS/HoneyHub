package fr.honeygroup.enumeration;

/**
 * Représente les différents états possibles d'un flux financier ou règlement (Payment) 
 * au sein du système Honey Group.
 * <p>
 * Ces statuts permettent de piloter le workflow de vérification comptable asynchrone 
 * par l'équipe de gestion, suite au téléversement des justificatifs de virement par les clients.
 * </p>
 */
public enum StatutPayment {

    /**
     * Statut initial par défaut d'un paiement dès sa soumission par le client.
     * <p>
     * Signifie que la pièce justificative (URL ou PDF) a été correctement réceptionnée 
     * par le système et se trouve en attente d'un contrôle visuel et bancaire de la part du gérant.
     * </p>
     */
    EN_VERIFICATION {
        @Override
        public boolean peutBasculerVers(StatutPayment nouveauStatut) {
            return nouveauStatut == VALIDE || nouveauStatut == REJETE;
        }
    },

    /**
     * Signifie que le gérant a validé manuellement la conformité du justificatif 
     * et a constaté l'apparition effective des fonds sur le compte bancaire de Honey Group.
     * <p>
     * Ce statut déclenche généralement la validation définitive de la réservation (Booking) associée.
     * </p>
     */
    VALIDE {
        @Override
        public boolean peutBasculerVers(StatutPayment nouveauStatut) {
            return false; // État final après validation
        }
    },

    /**
     * Signifie que le paiement a été refusé par l'équipe de gestion.
     * <p>
     * Ce cas de figure survient si le justificatif est illisible, falsifié, ou si le virement 
     * n'a jamais été reçu après le délai légal. Un rejet nécessite une nouvelle action du client.
     * </p>
     */
    REJETE {
        @Override
        public boolean peutBasculerVers(StatutPayment nouveauStatut) {
            return false; // État final après rejet
        }
    };

    /**
     * Valide de manière défensive si la transition vers un nouvel état est légale
     * selon les règles du workflow Honey Group.
     * * @param nouveauStatut Le statut cible vers lequel le paiement tente de migrer.
     * @return true si le changement d'état respecte le workflow, sinon false.
     */
    public abstract boolean peutBasculerVers(StatutPayment nouveauStatut);
}