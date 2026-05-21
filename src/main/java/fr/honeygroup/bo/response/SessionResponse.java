package fr.honeygroup.bo.response;

import fr.honeygroup.enumeration.StatutSession;
import lombok.Builder;
import lombok.Data;

/**
 * DTO (Data Transfer Object) de réponse encapsulant les informations d'une session 
 * écotouristique destinées à l'affichage sur les interfaces clients ou administratives.
 */
@Data
@Builder
public class SessionResponse {

    /** Identifiant technique unique de la session. */
    private Long id;

    /** Nom descriptif ou intitulé de la session de voyage. */
    private String nom;

    /** Détails complémentaires sur le contenu et le déroulement de la session. */
    private String description;

    /** État opérationnel actuel de la session dans son cycle de vie. */
    private StatutSession statut;

    /** Capacité totale d'accueil définie pour cette session. */
    private int capaciteMax;

    /** Nombre de places actuellement occupées par les réservations confirmées. */
    private int participantsActuels;

    // Ajoute ici d'autres champs métier nécessaires (ex: Date dateDebut, String lieu)
}