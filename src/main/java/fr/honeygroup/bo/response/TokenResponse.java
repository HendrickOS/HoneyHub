package fr.honeygroup.bo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de donnees (DTO) encapsulant le couple de jetons de securite distribues au client.
 * <p>
 * Ce payload constitue la reponse standard emise lors d'une authentification reussie ou d'une 
 * operation de rafraichissement de session. Il fournit les elements cryptographiques requis pour 
 * maintenir des requetes authentifiees et securisees sur l'API, sans etat cote serveur (Stateless).
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    /**
     * Le jeton d'acces (Access Token) au format JSON Web Token (JWT).
     * <p>
     * Ce jeton a une duree de vie courte et doit etre joint a l'entete HTTP (Authorization: Bearer) 
     * de chaque requete necessitant une habilitation particuliere.
     * </p>
     */
    private String accessToken;

    /**
     * Le jeton de rafraichissement (Refresh Token) utilise pour renouveler l'Access Token.
     * <p>
     * Ce jeton, a cycle de vie plus long, permet de solliciter l'API pour emettre une nouvelle 
     * paire de tokens sans exiger une reconnexon manuelle par saisie d'identifiants.
     * </p>
     */
    private String refreshToken;
}