package fr.honeygroup.bo.request;

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
    private LocalDateTime dateSouhaitee;

    @Min(value = 1, message = "Il faut au moins une personne")
    private Integer nbPersonnes;

    // Optionnel en Sandbox : l'ID de l'utilisateur connecté
    private Long userId;
}