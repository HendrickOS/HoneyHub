package fr.honeygroup.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "CIRCUIT") // Majuscules pour coller au script SQL
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Circuit extends Prestation {

    @NotBlank(message = "La description longue est obligatoire")
    @Size(min = 20, max = 5000)
    @Lob // Utilise @Lob pour le LONGTEXT de ton script
    @Column(name = "description_longue")
    private String descriptionLongue;

    @NotBlank(message = "L'itinéraire est obligatoire")
    @Lob // Pour stocker les étapes détaillées du voyage
    @Column(name = "itineraire", nullable = false)
    private String itineraire;

    @NotBlank(message = "La durée est obligatoire (ex: 7 jours / 6 nuits)")
    @Column(name = "duree", nullable = false, length = 100)
    private String duree;
}