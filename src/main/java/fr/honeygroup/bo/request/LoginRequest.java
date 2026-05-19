package fr.honeygroup.bo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de donnees (DTO) encapsulant les identifiants de connexion.
 * <p>
 * Ce payload est transmis a l'API lors de la phase d'authentification initiale. 
 * Il collecte le couple d'identifiants necessaire a la validation cryptographique 
 * par l'AuthenticationManager de la couche securite.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    /**
     * L'adresse email unique identifiant le compte de l'utilisateur.
     * <p>
     * Contraintes : Obligatoire, ne doit pas etre vide et doit obligatoirement respecter 
     * les specifications syntaxiques standard d'un email (format RFC).
     * </p>
     */
    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    private String email;

    /**
     * Le mot de passe associe au compte utilisateur, soumis en clair.
     * <p>
     * Contrainte : Obligatoire. Aucune contrainte de longueur ou de complexite n'est 
     * appliquee ici pour des raisons de securite algorithmique (eviter la divulgation 
     * preventive de politique de mots de passe aux clients non authentifies).
     * </p>
     */
    @NotBlank(message = "Le mot de passe est requis")
    private String password;
}