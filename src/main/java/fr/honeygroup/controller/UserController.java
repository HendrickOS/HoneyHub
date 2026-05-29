package fr.honeygroup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.honeygroup.bll.UserService;
import fr.honeygroup.bo.request.ProfileUpdateRequest;
import fr.honeygroup.bo.response.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controleur REST gerant le perimetre des utilisateurs et la gestion des profils.
 * <p>
 * Securise l'acces aux donnees privees du compte de l'utilisateur connecte et centralise
 * les requetes de consultation et de mise a jour du profil.
 * </p>
 * <p>
 * L'identification s'appuie sur le contexte de securite de Spring Security, garantissant
 * l'etancheite des donnees sans exposition d'identifiants bruts dans les URIs.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    /** Service metier pilotant la logique liee aux utilisateurs. */
    private final UserService userService;

    /**
     * Recupere les informations du profil de l'utilisateur actuellement authentifie.
     * <p>
     * L'extraction de l'identite se fait de maniere securisee via le jeton de session (JWT),
     * bloquant nativement toute tentative d'usurpation (IDOR).
     * </p>
     * * @param authentication L'objet d'authentification injecte par Spring Security, contenant le login du porteur du jeton.
     * @return Une ResponseEntity contenant le DTO UserProfileResponse du compte connecte et un code 200 OK.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUserProfile(authentication.getName()));
    }

    /**
     * Met a jour les coordonnees et options du profil de l'utilisateur connecte.
     * <p>
     * Intercepte le payload de requete, valide la conformite des donnees via Jakarta Bean Validation,
     * et applique les modifications uniquement sur le compte associe au jeton de securite actif.
     * </p>
     * * @param authentication L'objet d'authentification de Spring Security pour identifier le compte cible.
     * @param request Le DTO ProfileUpdateRequest contenant les nouvelles informations du profil validees.
     * @return Une ResponseEntity contenant le DTO UserProfileResponse mis a jour et un code 200 OK.
     */
    @PutMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(authentication.getName(), request));
    }
    
    /**
     * Recupere la liste exhaustive de tous les utilisateurs possedant le role CLIENT.
     * <p>
     * Cette operation est reservee aux administrateurs ou managers afin de permettre 
     * l'administration et le suivi de la base client.
     * </p>
     * <p>
     * La reponse est une collection paginee ou une liste complete (selon l'implémentation du service)
     * des profils clients enregistres dans le systeme.
     * </p>
     * * @return Une ResponseEntity contenant une liste de UserProfileResponse et un code 200 OK.
     */
    @GetMapping("/clients")
    public ResponseEntity<java.util.List<UserProfileResponse>> getAllClients() {
        return ResponseEntity.ok(userService.findAllClients());
    }
}