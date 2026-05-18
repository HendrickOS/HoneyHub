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
 * entrantes du Frontend avant traitement par la couche métier.
 * </p>
 */
@Data
public class LeadRequest {

    /**
     * Identifiant technique unique de l'utilisateur (client ou prospect) initiant la demande.
     */
    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
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
     * (notamment le pôle IT Outsourcing pour des besoins sur-mesure) sans sélectionner d'offre standardisée.
     */
    private Long prestationId;

    /**
     * Canal d'acquisition ou origine géographique/numérique de l'opportunité (ex: "Instagram", "Google", "Direct").
     */
    @NotBlank(message = "La source est obligatoire (ex: Instagram, Google, Direct)")
    @Size(min = 3, max = 50)
    private String source;

    /**
     * Bloc de texte optionnel permettant à l'utilisateur d'ajouter une note libre ou 
     * un commentaire contextuel lors de la saisie de son formulaire.
     */
    private String commentaireInterne;

    /**
     * Dictionnaire dynamique de critères complémentaires soumis via le formulaire de contact.
     * Permet de stocker des besoins sur-mesure sous forme de paires Clé/Valeur (EAV), 
     * validé obligatoire pour alimenter la table des détails spécifiques.
     */
    @NotNull(message = "Les détails du formulaire sont obligatoires")
    private Map<String, String> specificDetails;
}