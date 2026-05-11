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

    @Lob // Pour correspondre au LONGTEXT du SQL
    private String adresse;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^(\\+33|0)[1-9]\\d{8}$", message = "Format de téléphone invalide")
    @Column(length = 50)
    private String telephone;

    @Column(length = 100)
    private String pays;

    @Lob // Pour correspondre au LONGTEXT du SQL
    private String preferences;

    @OneToOne
    @MapsId // Indique que l'ID de cette entité est partagé avec l'ID de l'User
    @JoinColumn(name = "id")
    private User user;
}