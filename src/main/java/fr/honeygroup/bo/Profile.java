package fr.honeygroup.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "`PROFILE`") // Backticks car c'est aussi un mot réservé
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    private Long id; // Pas de @GeneratedValue ici car il utilise l'ID de l'User

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 5, max = 500, message = "Adresse invalide")
    @Lob
    private String adresse;
    @Pattern(
    	    regexp = "^\\+?[1-9]\\d{7,14}$",
    	    message = "Numéro de téléphone invalide (format international requis)"
    	)
    @Column(length = 50)
    private String telephone;

    @NotBlank(message = "Le pays est obligatoire")
    @Size(min = 2, max = 100, message = "Pays invalide")
    @Column(length = 100)
    private String pays;

    @Lob
    @Size(max = 1000, message = "Les préférences sont trop longues")
    private String preferences;

    @OneToOne
    @MapsId // Indique que l'ID de cette entité est partagé avec l'ID de l'User
    @JoinColumn(name = "id")
    private User user;
}