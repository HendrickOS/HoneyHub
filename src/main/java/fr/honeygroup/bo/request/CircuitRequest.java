package fr.honeygroup.bo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CircuitRequest extends PrestationRequest {

    @NotBlank(message = "La description longue est obligatoire")
    @Size(min = 20, max = 5000)
    private String descriptionLongue;

    @NotBlank(message = "L'itinéraire est obligatoire")
    private String itineraire;

    @NotBlank(message = "La durée est obligatoire (ex: 7 jours / 6 nuits)")
    private String duree;
}
