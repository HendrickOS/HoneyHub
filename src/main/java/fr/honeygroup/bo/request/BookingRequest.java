package fr.honeygroup.bo.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "L'ID de la prestation est obligatoire")
    private Long prestationId;

    @NotNull(message = "L'ID du pôle est obligatoire")
    private Long poleId;

    @NotNull(message = "La date de réservation est obligatoire")
    @Future(message = "La date de réservation doit être dans le futur")
    private LocalDateTime dateSouhaitee;

    @NotNull(message = "Le nombre de personnes est obligatoire")
    @Min(value = 1, message = "Il faut au moins une personne")
    private Integer nbPersonnes;

    /**
     * L'ID de l'utilisateur qui effectue la réservation.
     * Obligatoire pour lier la réservation au bon client en base de données.
     */
    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;
}