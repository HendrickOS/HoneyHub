package fr.honeygroup.bo.response;

import java.time.LocalDateTime;

import fr.honeygroup.enumeration.StatutSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) de réponse encapsulant les informations d'une session 
 * écotouristique destinées à l'affichage sur les interfaces clients ou administratives.
 * <p>
 * Ce conteneur a pour rôle de masquer la complexité du modèle de données (entités) 
 * et d'aplatir certaines informations (comme le nom de la prestation parente) pour 
 * optimiser les performances des applications clientes (Front-end ou Mobile).
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

    /** Identifiant technique unique de la session. */
    private Long id;

    /** * Identifiant de la prestation catalogue à laquelle appartient cette session.
     * Utile pour la navigation front-end (ex: générer un lien "Voir la prestation").
     */
    private Long prestationId;

    /** * Nom de la prestation associée (Aplatissement de données / Flattening).
     * <p>
     * Intégrer ce champ directement ici évite au front-end de devoir faire une 
     * seconde requête HTTP vers /api/prestations/{id} juste pour afficher le titre du voyage.
     * </p>
     */
    private String prestationNom;

    /** Date et heure de départ effectif du séjour. */
    private LocalDateTime dateDebut;

    /** Date et heure de fin ou de retour du séjour. */
    private LocalDateTime dateFin;

    /** État opérationnel actuel de la session dans son cycle de vie. */
    private StatutSession statut;

    /** Capacité totale d'accueil définie pour cette session. */
    private Integer capaciteMax;

    /** * Nombre de places actuellement occupées par les réservations confirmées. 
     * Correspond au champ "nbInscrits" de l'entité Session sous le capot.
     */
    private Integer participantsActuels;

}