package fr.honeygroup.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "PHOTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_photo")
    private Long id;

    @NotBlank(message = "L'URL du fichier est obligatoire")
    @Size(max = 500, message = "L'URL est trop longue (max 500 caractères)")
    @Column(name = "url_fichier", nullable = false, length = 500)
    private String urlFichier;

    @Size(max = 255, message = "La légende est trop longue (max 255 caractères)")
    @Column(name = "legende", length = 255)
    private String legende;
}