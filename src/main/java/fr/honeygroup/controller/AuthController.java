package fr.honeygroup.controller;

import fr.honeygroup.bll.AuthService;
import fr.honeygroup.bo.request.*;
import fr.honeygroup.bo.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controleur REST pilotant le systeme d'authentification et de securite des accès (IAM).
 * <p>
 * Centralise l'ensemble des operations publiques liees au cycle de vie de la session utilisateur :
 * l'inscription, l'emission de jetons d'acces cryptographiques (JWT), le rafraichissement
 * de session stateless, ainsi que les procedures securisees de reinitialisation de mot de passe.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    /** Service metier orchestrant la cryptographie, la validation des identites et l'emission des tokens. */
    private final AuthService authService;

    /**
     * Enregistre un nouvel utilisateur au sein du systeme d'information.
     * <p>
     * Endpoint public. Assure la reception du payload d'inscription, verifie la conformite des champs
     * via Jakarta Bean Validation et delegue le hachage securise du mot de passe a la couche BLL.
     * </p>
     * * @param request Le DTO RegisterRequest contenant les identifiants et informations du futur compte.
     * @return Une ResponseEntity contenant un message de succes au format JSON et un code 200 OK.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    /**
     * Authentifie un utilisateur et initie une session de type Stateless.
     * <p>
     * Endpoint public. Verifie la validite des identifiants (Email/Password) par rapport aux donnees 
     * persistees en base de donnees et genere le couple de jetons requis (Access Token & Refresh Token).
     * </p>
     * * @param request Le DTO LoginRequest contenant les informations d'identification de l'utilisateur.
     * @return Une ResponseEntity encapsulant le DTO TokenResponse (contenant le JWT) et un code 200 OK.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Renouvelle un jeton d'acces expire a partir d'un jeton de rafraichissement valide.
     * <p>
     * Endpoint public. Permet au client Front-end de maintenir la session active de maniere transparente
     * pour l'utilisateur sans requérir de re-saisie des identifiants, respectant les patterns de securite JWT.
     * </p>
     * * @param request Le DTO RefreshTokenRequest contenant le Refresh Token cryptographique de l'utilisateur.
     * @return Une ResponseEntity contenant la nouvelle paire de jetons (TokenResponse) et un code 200 OK.
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    /**
     * Invalide la session de l'utilisateur actuellement connecte.
     * <p>
     * Extrait l'identite du porteur du jeton depuis le contexte de securite actif et demande 
     * la revocation ou la destruction des references de jetons persistees cote serveur.
     * </p>
     * * @param authentication L'objet d'authentification injecte par Spring Security representant le compte actif.
     * @return Une ResponseEntity vide avec un code 200 OK pour confirmer la deconnexion.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        if (authentication != null) {
            authService.logout(authentication.getName());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Initie la procedure de recuperation de mot de passe en cas d'oubli.
     * <p>
     * Endpoint public. Verifie la presence de l'adresse email soumise et declenche la generation
     * d'un jeton temporaire a usage unique (Token à duree de vie limitee) transmis par canal separe.
     * </p>
     * * @param request Le DTO ForgotPasswordRequest contenant l'adresse de messagerie cible.
     * @return Une ResponseEntity vide avec un code 200 OK confirmant la prise en compte de la requete.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Consomme le jeton de reinitialisation temporaire pour appliquer un nouveau mot de passe.
     * <p>
     * Endpoint public. Verifie l'integrite et la non-expiration du token de recuperation fourni
     * avant de remplacer de maniere securisee l'empreinte cryptographique du mot de passe en base.
     * </p>
     * * @param request Le DTO ResetPasswordRequest incluant le jeton de validation et le nouveau mot de passe saisi.
     * @return Une ResponseEntity vide avec un code 200 OK confirmant la modification effective.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}