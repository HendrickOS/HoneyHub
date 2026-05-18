package fr.honeygroup.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.honeygroup.bll.BookingService;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Contrôleur REST exposant les points de terminaison (endpoints) de l'API pour la gestion des réservations (Booking).
 * <p>
 * Ce composant assure le routage des actions clients et des fonctionnalités d'administration du pôle Écotourisme.
 * Il applique la validation de surface des requêtes et orchestre la communication sécurisée avec la couche métier (BLL)
 * sous la racine d'URI {@code /api/bookings}.
 * </p>
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permet l'interconnexion globale de l'API avec les applications Frontend (React, Vue, Angular)
public class BookingController {

    private final BookingService bookingService;

    /**
     * Enregistre une nouvelle demande de réservation au sein du système en mode Sandbox.
     * <p>
     * Ce point d'accès intercepte le payload JSON, déclenche la validation Jakarta via {@code @Valid}, 
     * et retourne la ressource persistée enveloppée dans le code de statut HTTP sémantique approprié.
     * </p>
     * * @param request Objet DTO contenant les métadonnées de la réservation (session, participants).
     * @return Un {@link ResponseEntity} configuré sur le statut HTTP 201 CREATED et transportant le corps du {@link BookingResponse}.
     */
    @PostMapping("/reserve")
    public ResponseEntity<BookingResponse> creerReservation(@Valid @RequestBody BookingRequest request) {
        // Exécution de la logique métier (Mapping -> Calcul -> Sauvegarde)
        BookingResponse response = bookingService.creerReservationSandbox(request);
        
        // Retourne la réponse avec le statut 201 Created
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * VUE CLIENT : Extrait l'historique complet et exclusif des dossiers de l'utilisateur authentifié.
     * <p>
     * Approche sécurisée (Zéro ID dans l'URL) : L'identité de l'appelant est extraite de manière hermétique 
     * par le serveur via le contexte de sécurité, bloquant nativement toute tentative de manipulation IDOR.
     * </p>
     * * @return Un {@link ResponseEntity} contenant la liste des {@link BookingResponse} de l'appelant.
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMonHistorique() {
        // Appelle la méthode sécurisée du Service basée sur le SecurityContextHolder
        List<BookingResponse> historique = bookingService.getUtilisateurHistoriquePersonnel();
        return ResponseEntity.ok(historique);
    }
    
    /**
     * VUE STAFF (ADMIN/MANAGER) : Extrait l'historique d'activité d'un compte client ciblé.
     * <p>
     * Ce point d'accès est dédié au tableau de bord de gestion commerciale pour permettre au personnel 
     * de suivre et d'auditer les dossiers contractés par un utilisateur.
     * </p>
     * * @param userId Identifiant technique unique de l'utilisateur ciblé au sein du chemin d'accès.
     * @return Un {@link ResponseEntity} contenant la liste des réservations associées au compte client.
     */
    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getDossierClientPourStaff(@PathVariable(name = "userId") Long userId) {
        // Appel de la méthode de service dédiée à l'administration
        List<BookingResponse> dossier = bookingService.getDossierClientPourStaff(userId);
        return ResponseEntity.ok(dossier);
    }
    
    /**
     * VUE CLIENT : Soumet une demande officielle de résiliation sur un dossier de réservation existant.
     * <p>
     * Transitionne l'état du dossier vers une phase d'examen transitoire sans supprimer l'enregistrement, 
     * préservant la traçabilité de la demande.
     * </p>
     * * @param bookingId Identifiant technique unique de la réservation à modifier.
     * @return Un {@link ResponseEntity} contenant un message de confirmation textuel destiné à l'interface utilisateur.
     */
    @PutMapping("/cancel-request/{id}")
    public ResponseEntity<String> demanderAnnulation(@PathVariable(name = "id") Long bookingId) {
        bookingService.demanderAnnulation(bookingId);
        return ResponseEntity.ok("Votre demande d'annulation a bien été transmise au manager.");
    }

    /**
     * VUE STAFF (ADMIN) : Approuve et valide de manière définitive l'annulation d'un dossier.
     * <p>
     * L'usage de {@code @PatchMapping} matérialise une modification partielle de la ressource (mutation du statut métier). 
     * Cette action déclenche la libération automatique des places dans le calendrier des sessions.
     * </p>
     * * @param bookingId Identifiant technique unique de la réservation à clôturer.
     * @return Un {@link ResponseEntity} contenant le message textuel confirmant le succès de la clôture administrative.
     */
    @PatchMapping("/admin/approve-cancel/{id}")
    public ResponseEntity<String> approuverAnnulation(@PathVariable(name = "id") Long bookingId) {
        bookingService.approuverAnnulation(bookingId);
        return ResponseEntity.ok("La réservation a été officiellement annulée.");
    }
}