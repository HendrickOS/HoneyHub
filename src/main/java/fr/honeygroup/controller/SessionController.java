package fr.honeygroup.controller;

import fr.honeygroup.bll.SessionService;
import fr.honeygroup.bo.response.SessionResponse;
import fr.honeygroup.enumeration.StatutSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les points d'entrée pour la gestion des sessions écotouristiques.
 * <p>
 * Les opérations de lecture sont ouvertes, tandis que les modifications d'état 
 * sont réservées au personnel habilité (ADMIN, MANAGER).
 * </p>
 */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    /**
     * Récupère les détails d'une session spécifique.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionDetails(id));
    }

    /**
     * Pilote une transition de statut pour une session donnée.
     * Accessible uniquement aux administrateurs et managers.
     * * @param id Identifiant technique de la session.
     * @param nouveauStatut Le nouveau statut à appliquer (envoyé en paramètre de requête).
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