package enumeration;

/**
 * Énumération définissant les rôles et niveaux d'habilitation (RBAC) 
 * au sein de l'écosystème applicatif de Honey Group.
 * <p>
 * Ces rôles sont exploités par la couche de sécurité (Spring Security) 
 * pour restreindre l'accès aux points de terminaison de l'API et valider 
 * les droits de substitution dans la couche métier.
 * </p>
 */
public enum Role {
    
    /**
     * Administrateur de la plateforme. 
     * Possède des privilèges absolus sur l'ensemble de l'application, y compris 
     * la configuration technique globale et la gestion des comptes du personnel.
     */
    ADMIN,

    /**
     * Client final de Honey Group.
     * Profil usager standard pouvant consulter le catalogue, soumettre des demandes 
     * de leads techniques (IT) et contracter des réservations écotouristiques pour son propre compte.
     */
    CLIENT,

    /**
     * Gestionnaire ou Manager opérationnel (ex: Robert Samson).
     * Possède les droits d'audit commercial pour qualifier les leads, valider manuellement 
     * les preuves de paiement (uploads) et approuver les demandes d'annulation.
     */
    MANAGER
}