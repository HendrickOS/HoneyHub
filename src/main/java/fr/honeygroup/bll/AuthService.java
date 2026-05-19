package fr.honeygroup.bll;

import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.ForgotPasswordRequest;
import fr.honeygroup.bo.request.LoginRequest;
import fr.honeygroup.bo.request.RefreshTokenRequest;
import fr.honeygroup.bo.request.RegisterRequest;
import fr.honeygroup.bo.request.ResetPasswordRequest;
import fr.honeygroup.bo.response.TokenResponse;

/**
 * Contrat d'interface definissant la logique metier liee a l'authentification et la securite (IAM).
 * <p>
 * Ce service centralise les regles de gestion des acces, la validation cryptographique des identites, 
 * l'emission et le renouvellement de jetons de session sans etat (Stateless JWT), ainsi que les 
 * processus de securisation des comptes (inscription, deconnexion et reinitialisation de mot de passe).
 * </p>
 */
public interface AuthService {

    /**
     * Traite l'inscription d'un nouvel utilisateur et persiste son compte de maniere securisee.
     * <p>
     * Applique les verifications d'unicite des identifiants (email) et orchestre le hachage 
     * cryptographique du mot de passe en amont de l'insertion en base de donnees MySQL.
     * </p>
     * * @param request Le DTO RegisterRequest contenant les informations d'identification et de profil du candidat.
     * @return L'entite metier User generee et persistee, incluant son role affecte par defaut (CLIENT).
     */
    User register(RegisterRequest request);

    /**
     * Authentifie un utilisateur et genere un contexte de session Stateless via un couple de jetons.
     * <p>
     * Verifie la concordance de l'empreinte du mot de passe fourni avec le hachage stocke en base. 
     * En cas de succes, genere et retourne un jeton d'acces (Access Token) à courte duree de vie 
     * et un jeton de renouvellement (Refresh Token).
     * </p>
     * * @param request Le DTO LoginRequest contenant les identifiants de connexion (Email/Password).
     * @return Le DTO TokenResponse encapsulant les jetons cryptographiques generes pour le client.
     */
    TokenResponse login(LoginRequest request);

    /**
     * Renouvelle un jeton d'acces expire a partir d'un jeton de rafraichissement valide.
     * <p>
     * Verifie l'integrite, la non-expiration et la presence en base du Refresh Token soumis 
     * pour emettre une nouvelle paire de jetons sans imposer de reauthentification lourde a l'utilisateur.
     * </p>
     * * @param request Le DTO RefreshTokenRequest contenant le jeton de rafraichissement cryptographique.
     * @return Le DTO TokenResponse contenant la nouvelle paire de jetons valides.
     */
    TokenResponse refreshToken(RefreshTokenRequest request);

    /**
     * Invalide la session active de l'utilisateur et revoque ses acces actuels.
     * <p>
     * Supprime ou marque comme expires les jetons de rafraichissement associes a l'utilisateur 
     * en base de donnees pour neutraliser toute tentative de reutilisation malveillante.
     * </p>
     * * @param email L'adresse email unique de l'utilisateur demandant la deconnexion, extraite du contexte securise.
     */
    void logout(String email);

    /**
     * Initie la procedure securisee de recuperation en cas de mot de passe oublie.
     * <p>
     * Verifie l'existence de l'adresse email et genere un jeton temporaire a usage unique (Token d'oubli) 
     * associe a une date d'expiration stricte, avant de declencher l'envoi du canal de reinitialisation.
     * </p>
     * * @param request Le DTO ForgotPasswordRequest contenant l'adresse email de l'utilisateur.
     */
    void forgotPassword(ForgotPasswordRequest request);

    /**
     * Applique le nouveau mot de passe apres validation du jeton temporaire de recuperation.
     * <p>
     * Consomme le jeton d'oubli soumis, valide sa non-expiration, hache la nouvelle saisie utilisateur 
     * et met a jour l'empreinte de securite du compte en base de donnees.
     * </p>
     * * @param request Le DTO ResetPasswordRequest comprenant le token de reinitialisation et le nouveau mot de passe.
     */
    void resetPassword(ResetPasswordRequest request);
}