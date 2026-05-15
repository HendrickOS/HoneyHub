package fr.honeygroup.bo;

import enumeration.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "`USER`") // Backticks pour le mot réservé SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    @Column(nullable = false) // Correspond à 'password' dans ton SQL
    private String password;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Role role; // Valeur par défaut pour correspondre au SQL

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ -]{2,100}$", message = "Le nom contient des caractères invalides")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ -]{2,100}$", message = "Le prénom contient des caractères invalides")
    @Column(nullable = false, length = 100)
    private String prenom;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;
}