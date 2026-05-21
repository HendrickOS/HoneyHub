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
    EN_VERIFICATION,

    /**
     * Signifie que le gérant a validé manuellement la conformité du justificatif 
     * et a constaté l'apparition effective des fonds sur le compte bancaire de Honey Group.
     * <p>
     * Ce statut déclenche généralement la validation définitive de la réservation (Booking) associée.
     * </p>
     */
    VALIDE,

    /**
     * Signifie que le paiement a été refusé par l'équipe de gestion.
     * <p>
     * Ce cas de figure survient si le justificatif est illisible, falsifié, ou si le virement 
     * n'a jamais été reçu après le délai légal. Un rejet nécessite une nouvelle action du client.
     * </p>
     */
    REJETE
}