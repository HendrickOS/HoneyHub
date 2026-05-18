package fr.honeygroup.bo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Objet de transfert de données (DTO Request) encapsulant les informations nécessaires
 * à la création ou à la mise à jour d'un pôle d'activité (Pole).
 * <p>
 * Cette classe porte des contraintes de validation de surface (Jakarta Validation) connectées 
 * au système d'internationalisation (i18n), permettant une traduction dynamique des messages 
 * d'erreur renvoyés au Frontend.
 * </p>
 */
@Data
public class PoleRequest {

    /**
     * Libellé unique désignant le pôle d'activité (ex: "Écotourisme", "IT Outsourcing").
     * Lié à des clés d'internationalisation pour la gestion des messages de contraintes.
     */
    @NotBlank(message = "{pole.nom.required}")
    @Size(min = 3, max = 100, message = "{pole.nom.size}")
    private String nom;

    /**
     * Description textuelle présentant les activités, la vision et les services du pôle.
     * Soumise à un contrôle de longueur strict pour assurer la cohérence éditoriale sur le Frontend.
     */
    @NotBlank(message = "{pole.description.required}")
    @Size(min = 10, max = 1000, message = "{pole.description.size}")
    private String description;
}