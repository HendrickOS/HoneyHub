package fr.honeygroup.bo.request;

import java.time.LocalDateTime;

import fr.honeygroup.enumeration.StatutSession;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) de requête encapsulant les données nécessaires 
 * à la création ou à la mise à jour d'une session écotouristique.
 * <p>
 * Ce conteneur applique les contraintes de validation de surface (Bean Validation) 
 * requises avant le traitement par la couche métier (BLL), garantissant ainsi
 * l'absence de valeurs nulles ou aberrantes sur les axes temporels et relationnels.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionRequest {

    /**
     * Identifiant de la prestation catalogue à laquelle la session doit être structurellement rattachée.
     * Correspond à la clé étrangère présente dans l'entité Session.
     */
    @NotNull(message = "L'identifiant de la prestation associée est obligatoire")
    private Long prestationId;

    /**
     * Date et heure de départ effectif du séjour écotouristique.
     */
    @NotNull(message = "La date de début de la session est obligatoire")
    private LocalDateTime dateDebut;

    /**
     * Date et heure de fin ou de retour du séjour écotouristique.
     */
    @NotNull(message = "La date de fin de la session est obligatoire")
    private LocalDateTime dateFin;

    /**
     * Jauge maximale de participants autorisés simultanément sur ce créneau de voyage.
     * Utilisation de l'objet Integer pour permettre une validation de présence sémantique.
     */
    @NotNull(message = "La capacité maximale est obligatoire")
    @Min(value = 1, message = "La capacité maximale doit être supérieure ou égale à 1")
    private Integer capaciteMax;

    /**
     * État opérationnel de la session au sein de son cycle de vie (OUVERT, COMPLET, etc.).
     * <p>
     * Ce champ est optionnel lors d'une création pure (la base appliquant automatiquement 
     * le statut {@code OUVERT} par défaut via le cycle de vie de l'entité), mais s'avère 
     * indispensable lors d'une mise à jour globale.
     * </p>
     */
    private StatutSession statut;
}