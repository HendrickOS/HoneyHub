package fr.honeygroup.controller;

import fr.honeygroup.bll.SessionService;
import fr.honeygroup.bo.request.SessionRequest;
import fr.honeygroup.bo.response.SessionResponse;
import fr.honeygroup.enumeration.StatutSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les points d'entrée pour la gestion des sessions écotouristiques.
 * <p>
 * Les opérations de lecture (consultation catalogue) sont ouvertes, tandis que les 
 * modifications structurelles (création, mise à jour) et les transitions d'état 
 * sont strictement réservées au personnel habilité (ADMIN, MANAGER).
 * </p>
 */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    /**
     * Crée et publie une nouvelle session de voyage rattachée à une prestation.
     * <p>Opération restreinte aux administrateurs et managers.</p>
     *
     * @param request Le corps de la requête contenant les données d'initialisation (validées en surface).
     * @return Une réponse HTTP 201 (Created) contenant les détails de la ressource générée.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody SessionRequest request) {
        SessionResponse response = sessionService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Modifie les paramètres structurels d'une session existante (dates, capacité, etc.).
     * <p>Opération restreinte aux administrateurs et managers.</p>
     *
     * @param id L'identifiant technique unique de la session à mettre à jour.
     * @param request Le corps de la requête portant les nouvelles valeurs.
     * @return Une réponse HTTP 200 (OK) contenant l'état actualisé de la session.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<SessionResponse> updateSession(
            @PathVariable Long id, 
            @Valid @RequestBody SessionRequest request) {
        return ResponseEntity.ok(sessionService.updateSession(id, request));
    }

    /**
     * Récupère le détail exhaustif d'une session spécifique.
     * <p>Point d'entrée public pour la consultation via le front-end client.</p>
     *
     * @param id L'identifiant technique de la session cible.
     * @return Une réponse HTTP 200 (OK) contenant le DTO d'affichage.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionDetails(id));
    }

    /**
     * Pilote une transition de statut pour une session donnée en respectant l'automate métier.
     * <p>Opération restreinte aux administrateurs et managers.</p>
     *
     * @param id Identifiant technique de la session à faire évoluer.
     * @param nouveauStatut Le nouveau statut cible à appliquer (fourni en paramètre d'URL).
     * @return Une réponse HTTP 200 (OK) vide confirmant la réussite de l'opération.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/transition")
    public ResponseEntity<Void> transitionnerStatut(
            @PathVariable Long id, 
            @RequestParam StatutSession nouveauStatut) {
        
        sessionService.transitionnerStatut(id, nouveauStatut);
        return ResponseEntity.ok().build();
    }
}