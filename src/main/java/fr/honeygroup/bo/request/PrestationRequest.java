package fr.honeygroup.bo.request;

import enumeration.StatutPrestation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PrestationRequest {
    
    @NotNull(message = "Le pôle est obligatoire")
    private Long poleId;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 255)
    private String titreService;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 2000)
    private String description;
    
    @NotNull(message = "Le prix de base est obligatoire")
    private Double prixBase;

    private StatutPrestation statut;
}
