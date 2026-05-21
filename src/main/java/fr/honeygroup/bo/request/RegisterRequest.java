package fr.honeygroup.bo.request;

import fr.honeygroup.enumeration.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de donnees (DTO) encapsulant le formulaire de creation de compte.
 * <p>
 * Ce payload regroupe les attributs d'identite, de securite et de profil necessaires 
 * a l'enregistrement d'un nouvel utilisateur dans le systeme. Il applique des regles 
 * de validation strictes via Jakarta Bean Validation pour assainir les flux entrants.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    /**
     * Le nom de famille de l'utilisateur.
     * <p>
     * Contraintes : Obligatoire, compris entre 2 et 100 caracteres. Restreint aux lettres, 
     * caracteres accentues francophones, tirets et espaces via expression reguliere.
     * </p>
     */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ -]{2,100}$", message = "Le nom contient des caractères invalides")
    private String nom;

    /**
     * Le prenom de l'utilisateur.
     * <p>
     * Contraintes : Obligatoire, compris entre 2 et 100 caracteres. Valide selon les memes 
     * criteres alphabetiques stricts que le nom de famille.
     * </p>
     */
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ -]{2,100}$", message = "Le prénom contient des caractères invalides")
    private String prenom;

    /**
     * L'adresse email unique servant d'identifiant de connexion au systeme.
     * <p>
     * Contraintes : Obligatoire, doit valider la structure syntaxique standard RFC d'un email.
     * </p>
     */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
     
    /**
     * Le numero de telephone de contact.
     * <p>
     * Contraintes : Obligatoire, doit respecter la recommandation internationale E.164 
     * (prefixe optionnel + suivi de 7 a 14 chiffres) pour la compatibilite des notifications SMS.
     * </p>
     */
    @Pattern(
            regexp = "^\\+?[1-9]\\d{7,14}$",
            message = "Numéro de téléphone invalide (format international requis)"
        )
    @NotBlank(message = "L'telephone est obligatoire")
    private String telephone;

    /**
     * Le mot de passe en clair soumis lors de la creation du compte.
     * <p>
     * Contraintes : Obligatoire, impose une longueur minimale defensive de 8 caracteres 
     * avant sa prise en charge par l'encodeur de hachage de la couche BLL.
     * </p>
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    private String password;
    
    /**
     * L'adresse postale de residence de l'utilisateur.
     * <p>
     * Contraintes : Obligatoire, comprise entre 5 et 500 caracteres. Representee en tant que Large Object 
     * (@Lob) pour anticiper les structures de saisies d'adresses complexes ou multilignes.
     * </p>
     */
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 5, max = 500, message = "Adresse invalide")
    @Lob
    private String adresse;
    
    /**
     * Le pays de residence de l'utilisateur.
     * <p>
     * Contraintes : Obligatoire, taille bornee entre 2 et 100 caracteres.
     * </p>
     */
    @NotBlank(message = "Le pays est obligatoire")
    @Size(min = 2, max = 100, message = "Pays invalide")
    @Column(length = 100)
    private String pays;
    
    /**
     * Bloc textuel optionnel decrivant les preferences ou besoins particuliers du client.
     * <p>
     * Contraintes : Limite a un maximum de 1000 caracteres, mappe sous forme d'objet large (@Lob).
     * </p>
     */
    @Lob
    @Size(max = 1000, message = "Les préférences sont trop longues")
    private String preferences;

    /**
     * Le niveau de privilege ou l'habilitation associe au compte.
     * <p>
     * Mappe sous forme de chaine de caracteres (EnumType.STRING) basee sur l'enumeration Role 
     * (ex: CLIENT, ADMIN, STAFF).
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;
}