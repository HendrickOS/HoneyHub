package fr.honeygroup.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.honeygroup.bll.BookingService;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permet les requêtes depuis ton front-end (React/Vue/etc.)
public class BookingController {

    private final BookingService bookingService;

    /**
     * Endpoint pour créer une réservation en mode Sandbox.
     * Accessible via POST http://localhost:8080/api/bookings/reserve
     */
    @PostMapping("/reserve")
    public ResponseEntity<BookingResponse> creerReservation(@Valid @RequestBody BookingRequest request) {
        // Exécution de la logique métier (Mapping -> Calcul -> Sauvegarde)
        BookingResponse response = bookingService.creerReservationSandbox(request);
        
        // Retourne la réponse avec le statut 201 Created
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * VUE CLIENT : Récupérer son propre historique de manière sécurisée.
     * Plus d'ID dans l'URL : on utilise l'identité de celui qui est connecté.
     * GET http://localhost:8080/api/bookings/my-bookings
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMonHistorique() {
        // Appelle la nouvelle méthode sécurisée du Service
        List<BookingResponse> historique = bookingService.getUtilisateurHistoriquePersonnel();
        return ResponseEntity.ok(historique);
    }
    
    /**
     * Dashboard Staff : Récupérer toutes les réservations d'un utilisateur spécifique.
     * Accessible par : MANAGER, ADMIN
     * GET http://localhost:8080/api/bookings/admin/user/{userId}
     */
    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getDossierClientPourStaff(@PathVariable(name = "userId") Long userId) {
        // Appel de la méthode de service qu'on a préparée
        List<BookingResponse> dossier = bookingService.getDossierClientPourStaff(userId);
        return ResponseEntity.ok(dossier);
    }
}