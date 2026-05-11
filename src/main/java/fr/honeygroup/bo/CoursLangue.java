package fr.honeygroup.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "COURS_LANGUE") // Respecte la casse de ton script SQL
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CoursLangue extends Prestation {

    @NotBlank(message = "La langue enseignée est obligatoire")
    @Size(max = 100)
    @Column(name = "langue", nullable = false, length = 100)
    private String langue;

    @NotBlank(message = "Le niveau requis ou visé est obligatoire (ex: B1, Débutant...)")
    @Size(max = 50)
    @Column(name = "niveau", nullable = false, length = 50)
    private String niveau;

    @NotBlank(message = "Le descriptif du programme est obligatoire")
    @Size(min = 20, max = 5000)
    @Lob // Pour correspondre au LONGTEXT de ton script SQL
    @Column(name = "descriptif_programme")
    private String descriptifProgramme;
}