package fr.honeygroup.bo.response;

import lombok.Builder;
import lombok.Data;

/**
 * Objet de transfert de données (DTO Response) modélisant la réponse épurée 
 * d'un pôle d'activité (Pole) renvoyée par l'API.
 * <p>
 * Ce DTO est utilisé pour présenter l'identité macroscopique des pôles (Écotourisme, IT Outsourcing) 
 * sur les interfaces front-end (menus, fiches de présentation) sans charger inutilement 
 * la collection des prestations associées.
 * </p>
 */
@Data
@Builder
public class PoleResponse {

    /**
     * Identifiant technique unique du pôle d'activité (mappé sur la clé primaire de la base de données).
     */
    private Long id;

    /**
     * Libellé nominatif du pôle d'activité (ex: "Écotourisme", "IT Outsourcing").
     */
    private String nom;

    /**
     * Description textuelle ou éditoriale présentant le périmètre d'action du pôle.
     */
    private String description;
}