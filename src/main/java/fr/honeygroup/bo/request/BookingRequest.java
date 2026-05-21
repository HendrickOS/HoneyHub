package fr.honeygroup.bo.request;

import enumeration.TypeReservation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) encapsulant les données d'entrée 
 * lors d'une requête de création de réservation par le Frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    /**
     * L'ID de l'utilisateur qui effectue la réservation.
     * Optionnel à la soumission car sécurisé et forcé par le contexte Spring Security.
     */
    private Long userId;

    /**
     * L'ID de la session de voyage choisie par l'utilisateur.
     * Remplace définitivement prestationId, poleId et dateSouhaitee.
     */
    @NotNull(message = "L'ID de la session est obligatoire")
    private Long sessionId;

    /**
     * Le nombre de places que le client souhaite réserver pour ce voyage.
     * Permet le contrôle de la jauge côté service.
     */
    @NotNull(message = "Le nombre de personnes est obligatoire")
    @Min(value = 1, message = "Il faut au moins une personne")
    private Integer nbPersonnes;
    
    /**
     * Le type de la réservation sélectionné par le client.
     * Permet d'orienter le workflow métier (ex: "SESSION" pour le catalogue, 
     * "SUR_MESURE" pour l'IT).
     */
    @NotNull(message = "Le type de réservation est obligatoire")
    private TypeReservation typeReservation;
}