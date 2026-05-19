package fr.honeygroup.bo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de donnees (DTO) encapsulant la requete de renouvellement de session.
 * <p>
 * Ce payload est transmis a l'API lorsque le jeton d'acces (Access Token JWT) a expire. 
 * Il contient uniquement le jeton de rafraichissement permettant a la couche BLL de verifier 
 * l'identite et d'emettre une nouvelle paire de tokens sans forcer l'utilisateur a se reconnecter.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {

    /**
     * Le jeton cryptographique de rafraichissement (Refresh Token) genere initialement lors du login.
     * <p>
     * Contrainte : Ne peut pas etre nul, vide ou compose uniquement d'espaces blancs. 
     * Il est generalement passe sous forme d'une chaine de caracteres unique (UUID v4).
     * </p>
     */
    @NotBlank(message = "Le refresh token est requis")
    private String refreshToken;
}