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
public class CoursLangueRequest extends PrestationRequest {

    @NotBlank(message = "La langue enseignée est obligatoire")
    @Size(max = 100)
    private String langue;

    @NotBlank(message = "Le niveau requis ou visé est obligatoire (ex: B1, Débutant...)")
    @Size(max = 50)
    private String niveau;

    @NotBlank(message = "Le descriptif du programme est obligatoire")
    @Size(min = 20, max = 5000)
    private String descriptifProgramme;
}
