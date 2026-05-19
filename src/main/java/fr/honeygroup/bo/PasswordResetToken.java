package fr.honeygroup.bo;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entite JPA representant les jetons temporaires de reinitialisation de mot de passe.
 * <p>
 * Cette table assure le stockage et le controle des jetons a usage unique (Single-use UUID)
 * generes lors des demandes de recuperation de compte. Elle permet d'associer de maniere 
 * ephemere un code secret d'oubli a un utilisateur cible avec une date limite de validite.
 * </p>
 */
@Entity
@Table(name = "PASSWORD_RESET_TOKEN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    /** Identifiant unique et technique du jeton en base de donnees (Cle primaire auto-incrementee). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le jeton de securite unique stocke sous forme de chaine de caracteres. */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Association directe avec l'utilisateur ayant formule la demande de changement.
     * <p>
     * Le mode de chargement est configure de maniere explicite en {@link FetchType#EAGER} car 
     * l'extraction de l'utilisateur associe est indispensable et systematique lors de l'etape 
     * de consommation et de validation du jeton par la couche BLL.
     * </p>
     */
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    /** Horodatage specifiant la date et l'heure precises d'expiration du jeton de reinitialisation. */
    @Column(nullable = false)
    private LocalDateTime expiryDate;
}