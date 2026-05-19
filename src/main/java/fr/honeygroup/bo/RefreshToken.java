package fr.honeygroup.bo;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Entite JPA representant les jetons de rafraichissement (Refresh Tokens) pour la securite.
 * <p>
 * Cette table assure la persistance des jetons cryptographiques a longue duree de vie.
 * Elle permet de maintenir des sessions de type Stateless (JWT) tout en conservant un controle
 * cote serveur sur la validite, l'expiration et la revocation (logout) des acces d'un utilisateur.
 * </p>
 */
@Entity
@Table(name = "REFRESH_TOKEN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    /** Identifiant unique et technique du jeton en base de donnees (Cle primaire auto-incrementee). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le jeton cryptographique unique (generalement un UUID v4 genere sous forme de chaine). */
    @Column(nullable = false, unique = true)
    private String token;

    /** * Liaison bidirectionnelle stricte avec l'utilisateur proprietaire du jeton.
     * <p>
     * La contrainte d'unicite (unique = true) garantit une strategie de session unique par compte (single-session) :
     * un utilisateur ne peut posseder qu'un seul jeton de rafraichissement actif en base de donnees.
     * </p>
     */
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true, nullable = false)
    private User user;

    /** Point de repere temporel specifiant la date et l'heure limites de validite du jeton (Format Unix UTC Instant). */
    @Column(nullable = false)
    private Instant expiryDate;
}