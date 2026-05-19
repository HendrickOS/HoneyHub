package fr.honeygroup.bo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de donnees (DTO) representant la demande initiale d'oubli de mot de passe.
 * <p>
 * Ce payload est emis par le client lorsqu'il initie la procedure de recuperation. 
 * Il sert a transmettre l'adresse de contact a la couche BLL pour verifier l'existence 
 * du compte et declencher l'emission du jeton temporaire de reinitialisation.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {

    /**
     * L'adresse email associee au compte utilisateur a recuperer.
     * <p>
     * Contraintes : Obligatoire, ne doit pas etre vide et doit valider le format syntaxique 
     * d'un email standard afin de securiser l'etape de recherche dans le referentiel.
     * </p>
     */
    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    private String email;
}