package fr.honeygroup.bo.request;

import enumeration.Role;
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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ -]{2,100}$", message = "Le nom contient des caractères invalides")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ -]{2,100}$", message = "Le prénom contient des caractères invalides")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
     
    @Pattern(
    	    regexp = "^\\+?[1-9]\\d{7,14}$",
    	    message = "Numéro de téléphone invalide (format international requis)"
    	)
    @NotBlank(message = "L'telephone est obligatoire")
    private String telephone;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    private String password;
    
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 5, max = 500, message = "Adresse invalide")
    @Lob
    private String adresse;
    
    @NotBlank(message = "Le pays est obligatoire")
    @Size(min = 2, max = 100, message = "Pays invalide")
    @Column(length = 100)
    private String pays;
    
    @Lob
    @Size(max = 1000, message = "Les préférences sont trop longues")
    private String preferences;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;
}
