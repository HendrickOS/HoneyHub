package fr.honeygroup.bo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PoleRequest {

    @NotBlank(message = "{pole.nom.required}")
    @Size(min = 3, max = 100, message = "{pole.nom.size}")
    // On pourrait ajouter un @Pattern ici si on veut interdire certains caractères
    private String nom;

    @NotBlank(message = "{pole.description.required}")
    @Size(min = 10, max = 1000, message = "{pole.description.size}")
    private String description;
}