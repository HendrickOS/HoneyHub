package fr.honeygroup.bo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de donnees (DTO) representant la requete de reinitialisation de mot de passe.
 * <p>
 * Ce payload est utilise lors de la derniere etape du workflow de recuperation de compte. 
 * Il transporte le jeton de validation ephemere ainsi que la nouvelle empreinte secrete choisie 
 * par l'utilisateur, tout en appliquant des contraintes de validation de surface.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    /** * Jeton cryptographique d'oubli (UUID) recupere par l'utilisateur (via le canal de reinitialisation).
     * <p>
     * Contrainte : Ne peut pas etre nul, vide ou compose uniquement d'espaces blancs.
     * </p>
     */
    @NotBlank(message = "Le token est requis")
    private String token;

    /** * Le nouveau mot de passe choisi par l'utilisateur pour remplacer l'ancienne empreinte hachee.
     * <p>
     * Contraintes : Ne peut pas etre vide et doit obligatoirement respecter une politique de complexite 
     * minimale fixee a 8 caracteres pour la securite du compte.
     * </p>
     */
    @NotBlank(message = "Le nouveau mot de passe est requis")
    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caracteres")
    private String newPassword;
}