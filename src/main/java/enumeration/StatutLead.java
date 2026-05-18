package enumeration;

/**
 * Énumération modélisant les étapes successives du tunnel de conversion commerciale (CRM)
 * au sein du système de Honey Group.
 * <p>
 * Ces constantes permettent de suivre le cycle de vie des opportunités d'affaires (Leads), 
 * depuis la capture initiale du besoin utilisateur sur la plateforme Web jusqu'à sa résolution 
 * finale ou sa transformation contractuelle.
 * </p>
 */
public enum StatutLead {
    
    /**
     * État initial d'une opportunité d'affaires venant d'être soumise par un prospect.
     * Le dossier est en attente d'attribution et d'analyse préliminaire par l'équipe commerciale.
     */
    NOUVEAU,

    /**
     * Le lead est pris en charge par un gestionnaire. 
     * Cette phase implique l'analyse des spécifications techniques complémentaires, 
     * les prises de contact ou l'établissement de devis sur-mesure (notamment pour l'IT Outsourcing).
     */
    EN_COURS,

    /**
     * Clôture positive du dossier de prospection. 
     * La demande d'informations ou le besoin a été traité avec succès et a fait l'objet 
     * d'une réponse administrative ou d'une finalisation de conseil.
     */
    TRAITE,

    /**
     * Fin de cycle faisant suite au rejet du lead par l'organisation (ex: demande non sérieuse, 
     * hors périmètre de compétences technologiques du pôle IT, ou budget incompatible).
     */
    REFUSE,

    /**
     * Succès commercial majeur du workflow.
     * Représente la transformation d'une opportunité d'affaires écotouristique en un dossier ferme, 
     * déclenchant la génération d'une entité de réservation {@link fr.honeygroup.bo.Booking}.
     */
    CONVERTI
}