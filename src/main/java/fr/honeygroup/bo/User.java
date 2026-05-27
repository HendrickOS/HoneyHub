package fr.honeygroup.bo;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import fr.honeygroup.enumeration.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entité représentant un utilisateur (User) au sein du système Honey Group.
 * <p>
 * Cette classe centralise les informations d'authentification, de contrôle d'accès 
 * par rôles (RBAC) ainsi que l'identité civile de chaque acteur du système (Clients, 
 * Managers, Administrateurs).
 * </p>
 */
@Entity
@Table(name = "`USER`") // Usage des backticks indispensable car USER est un mot réservé dans de nombreux SGBD (comme MariaDB/H2)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;
    /**
     * Identifiant unique et clé primaire de l'utilisateur.
     * Généré de façon séquentielle par auto-incrémentation native dans la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Adresse de courrier électronique servant d'identifiant d'authentification unique.
     * Fait l'objet d'une contrainte d'unicité au niveau de la table pour empêcher les doublons.
     */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Empreinte sécurisée (hash) du mot de passe de l'utilisateur.
     * Stockée après chiffrement (généralement via BCrypt par Spring Security) pour des raisons de conformité.
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    @Column(nullable = false)
    private String password;

    /**
     * Rôle ou niveau d'habilitation applicative de l'utilisateur (ex: CLIENT, MANAGER, ADMIN).
     * Persisté explicitement sous sa forme textuelle (STRING) afin de faciliter la maintenance de la base de données.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    /**
     * Nom de famille de l'utilisateur.
     * Limité par une expression régulière acceptant uniquement les caractères alphabétiques et diacritiques courants.
     */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ -]{2,100}$", message = "Le nom contains des caractères invalides")
    @Column(nullable = false, length = 100)
    private String nom;

    /**
     * Prénom de l'utilisateur.
     * Soumis aux mêmes règles sanitaires de validation de format que le nom.
     */
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ -]{2,100}$", message = "Le prénom contient des caractères invalides")
    @Column(nullable = false, length = 100)
    private String prenom;
    
    /**
     * Profil complémentaire étendu rattaché à cet utilisateur.
     * Relation un-à-un bidirectionnelle. Les opérations d'écriture et de suppression sont propagées en cascade.
     * L'annotation @ToString.Exclude évite les récursions infinies lors de l'appel automatique de la méthode toString.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
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