package enumeration;

/**
 * Énumération des types de services proposés par Honey Group.
 * <p>
 * Utilisée pour distinguer les flux de réservations standardisés (tourisme)
 * des projets de développement spécifiques (IT Outsourcing).
 * </p>
 */
public enum TypeReservation {
    /** Prestation liée à une session de voyage définie dans le catalogue (ex: Trek). */
    SESSION, 
    
    /** Projet spécifique et personnalisé nécessitant un suivi particulier (ex: IT). */
    SUR_MESURE
}