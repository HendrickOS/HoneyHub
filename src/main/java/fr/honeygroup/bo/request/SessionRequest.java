package fr.honeygroup.bo.request;

import fr.honeygroup.enumeration.StatutSession;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO (Data Transfer Object) de requête encapsulant les données nécessaires 
 * à la création ou à la mise à jour d'une session écotouristique.
 */
@Data
public class SessionRequest {

    /** Intitulé ou nom de la session de voyage à créer ou modifier. */
    private String nom;

    /** Description détaillée du contenu et des spécificités du voyage. */
    private String description;

    /** * État initial ou nouveau statut à appliquer à la session.
     * Ce champ est obligatoire pour garantir la cohérence du cycle de vie.
     */
    @NotNull(message = "Le statut est obligatoire")
    private StatutSession statut;

    /** Nombre maximal de participants autorisés pour cette session. */
    private int capaciteMax;
}