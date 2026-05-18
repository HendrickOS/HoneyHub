package enumeration;

/**
 * Cycle de vie opérationnel d'une session fixe du pôle Écotourisme.
 */
public enum StatutSession {
    /** La session accepte activement les réservations clients. */
    OUVERT,
    
    /** Le nombre maximal de participants est atteint, les inscriptions sont bloquées. */
    COMPLET,

    /** Le circuit ou voyage est actuellement en cours de réalisation sur le terrain. */
    EN_COURS,
    
    /** Le voyage est terminé, les clients sont rentrés (clôture du dossier). */
    CLOTURE,
    
    /** Le départ a été annulé (raisons climatiques, logistiques, etc.). */
    ANNULE
}