package fr.honeygroup.bo.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Map;

@Data
public class LeadRequest {

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "L'ID du pôle est obligatoire") // Important pour ton SQL
    private Long poleId;

    private Long prestationId; // Optionnel car on peut contacter un pôle sans choisir de circuit précis

    @NotBlank(message = "La source est obligatoire (ex: Instagram, Google, Direct)")
    @Size(min = 3, max = 50)
    private String source;

    private String commentaireInterne; // Optionnel : si le client veut ajouter une note

    @NotNull(message = "Les détails du formulaire sont obligatoires")
    private Map<String, String> specificDetails;
}