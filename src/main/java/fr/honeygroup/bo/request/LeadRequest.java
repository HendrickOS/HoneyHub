package fr.honeygroup.bo.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Map;

/**
 * Objet de transfert de données (DTO Request) encapsulant les informations requises 
 * pour soumettre une nouvelle opportunité commerciale ou demande de contact (Lead).
 * <p>
 * Cette classe porte les contraintes de validation de surface (Jakarta Validation) 
 * appliquées dès la réception de la requête HTTP afin de sécuriser l'intégrité des données 
 * entrantes du Frontend avant traitement par la couche métier. Elle prend en charge de manière 
 * polymorphe les requêtes issues de clients authentifiés ou de visiteurs anonymes.
 * </p>
 */
@Data
public class LeadRequest {

    /**
     * Nom ou identité renseignée par le contact lors de la saisie du formulaire.
     */
    @Size(min = 2, max = 100)
    private String nom;
    
    /**
     * Adresse électronique de contact pour le suivi commercial du dossier.
     */
    @Email(message = "Email invalide")
    private String email;

    /**
     * Identifiant technique unique de l'utilisateur (client ou prospect enregistré).
     * Note d'architecture : Ce champ est facultatif (nullable). S'il est omis, le système interprète 
     * la demande comme provenant d'un visiteur anonyme et s'appuie sur les champs nom et email.
     */
    // @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;

    /**
     * Identifiant unique du pôle d'activité concerné par la demande (Écotourisme ou IT Outsourcing).
     * Alignement strict indispensable avec la structure relationnelle de la base de données.
     */
    @NotNull(message = "L'ID du pôle est obligatoire")
    private Long poleId;

    /**
     * Identifiant optionnel d'une prestation spécifique du catalogue.
     * Laissée volontairement optionnelle afin de permettre à un prospect de contacter un pôle 
     * (notamment pour des besoins d'hébergements complexes ou de services sur-mesure) sans sélectionner d'offre standardisée.
     */
    private Long prestationId;

    /**
     * Canal d'acquisition ou origine géographique/numérique de l'opportunité (ex: "Instagram", "Google", "Direct").
     */
    @NotBlank(message = "La source est obligatoire (ex: Instagram, Google, Direct)")
    @Size(min = 3, max = 50)
    private String source;

    /* * Note de maintenance : Propriété supprimée du formulaire public d'entrée. 
     * L'historique des notes et échanges internes est géré exclusivement en aval par le personnel Staff 
     * au niveau de la couche de persistance ou du CRM.
     * * private String commentaireInterne;
     */

    /**
     * Dictionnaire dynamique de critères complémentaires soumis via le formulaire de contact.
     * Permet de stocker des besoins sur-mesure sous forme de paires Clé/Valeur (EAV), 
     * validé obligatoire pour alimenter la table des détails spécifiques.
     */
    @NotNull(message = "Les détails du formulaire sont obligatoires")
    private Map<String, String> specificDetails;
}