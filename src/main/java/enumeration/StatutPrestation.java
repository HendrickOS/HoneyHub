package enumeration;

/**
 * Énumération modélisant les états de visibilité et de cycle de vie des prestations
 * au sein du catalogue de Honey Group.
 * <p>
 * Ces constantes permettent de piloter dynamiquement la disponibilité commerciale des offres 
 * (circuits écotouristiques, cours de langue) sur la plateforme, assurant l'étanchéité entre 
 * les phases de conception, d'exploitation et d'archivage.
 * </p>
 */
public enum StatutPrestation {
    
    /**
     * La prestation est officiellement ouverte et visible sur la plateforme.
     * Les clients peuvent la consulter et y souscrire ou planifier des sessions associées.
     */
    ACTIF,

    /**
     * La prestation est temporairement retirée de la vente (ex: maintenance d'un circuit, 
     * indisponibilité saisonnière ou réajustement logistique).
     * Elle reste enregistrée en base de données mais n'est plus accessible pour de nouvelles réservations.
     */
    INACTIF,

    /**
     * Retrait définitif du catalogue commercial.
     * La prestation est conservée uniquement à des fins d'historisation comptable et d'audit 
     * pour ne pas corrompre l'intégrité référentielle des anciennes réservations en base de données.
     */
    ARCHIVE,

    /**
     * Phase de conception ou de relecture technique (ex: nouveau circuit en cours de structuration 
     * ou module de formation en attente de validation tarifaire par la direction).
     * Invisible pour les clients finaux.
     */
    EN_ATTENTE
}