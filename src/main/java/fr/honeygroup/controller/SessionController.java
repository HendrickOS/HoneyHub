package fr.honeygroup.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.honeygroup.bll.SessionService;
import fr.honeygroup.bo.request.SessionRequest;
import fr.honeygroup.bo.response.SessionResponse;
import fr.honeygroup.enumeration.StatutSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
@CrossOrigin
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
            @PathVariable(name = "id") Long id, 
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
    public ResponseEntity<SessionResponse> getSession(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(sessionService.getSessionDetails(id));
    }

    /**
     * Pilote une transition de statut pour une session donnée en respectant l'automate métier.
     * <p>
     * Cette opération vérifie la conformité de la transition par rapport aux règles définies 
     * dans l'énumération {@link StatutSession}. Seuls les administrateurs et managers 
     * sont autorisés à modifier l'état opérationnel d'une session.
     * </p>
     *
     * @param id            Identifiant technique unique de la session à faire évoluer.
     * @param nouveauStatut Le nouveau statut cible à appliquer.
     * @return Une réponse HTTP 200 (OK) contenant un message récapitulatif du changement d'état.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/transition")
    public ResponseEntity<String> transitionnerStatut(
            @PathVariable(name = "id") Long id, 
            @RequestParam("nouveauStatut") StatutSession nouveauStatut) {
        
        // Récupération de l'ancien statut pour construire le message de confirmation
        String ancienStatut = sessionService.transitionnerStatut(id, nouveauStatut);
        
        String message = String.format("Succès : Statut de la session %d mis à jour de %s vers %s.", 
                                        id, ancienStatut, nouveauStatut.name());
        
        return ResponseEntity.ok(message);
    }
}