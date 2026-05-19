package fr.honeygroup.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entité représentant le profil étendu (Profile) d'un utilisateur au sein du système.
 * <p>
 * Cette classe stocke les coordonnées personnelles et géographiques ainsi que les préférences 
 * spécifiques d'un utilisateur. Elle partage sa clé primaire avec l'entité {@link User} 
 * via une relation un-à-un partagée pour garantir une intégrité référentielle stricte.
 * </p>
 */
@Entity
@Table(name = "`PROFILE`") // Utilisation des backticks indispensable car PROFILE est un mot clé réservé dans de nombreuses bases de données
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    /**
     * Identifiant unique et clé primaire du profil.
     * Cette valeur n'est pas auto-incrémentée car elle hérite et s'aligne directement 
     * sur l'identifiant technique de l'utilisateur associé grâce à l'annotation {@link MapsId}.
     */
    @Id
    private Long id;

    /**
     * Adresse postale complète de résidence de l'utilisateur.
     * L'annotation @Lob mappe ce champ en tant qu'objet textuel de taille importante (TEXT/LONGTEXT) en base MariaDB.
     */
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 5, max = 500, message = "Adresse invalide")
    @Lob
    private String adresse;

    /**
     * Numéro de téléphone de contact principal de l'utilisateur.
     * Soumis à une expression régulière restrictive permettant les formats nationaux et internationaux.
     */
    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Numéro de téléphone invalide (format international requis)")
    @Column(length = 50)
    private String telephone;

    /**
     * Pays de résidence actuel déclaré par l'utilisateur.
     */
    @NotBlank(message = "Le pays est obligatoire")
    @Size(min = 2, max = 100, message = "Pays invalide")
    @Column(length = 100)
    private String pays;

    /**
     * Zone de texte libre mémorisant les choix préférentiels, contraintes alimentaires 
     * ou notes spécifiques pour les réservations écotouristiques.
     * L'annotation @Lob permet d'y stocker de longs volumes de chaînes ou des structures textuelles de type JSON.
     */
    @Lob
    @Size(max = 1000, message = "Les préférences sont trop longues")
    private String preferences;

    /**
     * L'utilisateur propriétaire et rattaché à ce profil.
     * L'annotation @MapsId indique que la clé primaire de cette entité est calquée sur celle de l'User.
     * L'annotation @ToString.Exclude prévient toute récursion cyclique infinie lors des appels de journalisation de la classe.
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @ToString.Exclude
    private User user;
}