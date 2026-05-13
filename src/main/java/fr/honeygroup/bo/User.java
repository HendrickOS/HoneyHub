package fr.honeygroup.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import enumeration.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "`USER`") // Backticks pour le mot réservé SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}