package fr.honeygroup.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.honeygroup.bll.PaymentService;
import fr.honeygroup.bo.request.PaymentRequest;
import fr.honeygroup.bo.response.PaymentResponse;
import lombok.RequiredArgsConstructor;

/**
 * Contrôleur REST exposant les points d'entrée pour la gestion des paiements.
 * <p>
 * Ce contrôleur est protégé par des règles de sécurité strictes : 
 * les opérations critiques de validation et de rejet sont réservées au personnel (ADMIN, MANAGER).
 * </p>
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Récupère les détails d'un paiement spécifique.
     * Accessible à l'utilisateur propriétaire ou au personnel.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(paymentService.getPaymentDetails(id));
    }

    /**
     * Récupère l'historique des paiements pour une réservation donnée.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityService.isOwnerOfBooking(#bookingId)")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByBooking(@PathVariable(name = "bookingId") Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBooking(bookingId));
    }

    /**
     * Valide un paiement (Action réservée au Staff).
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/valider")
    public ResponseEntity<Void> validerPaiement(@PathVariable(name = "id") Long id) {
        paymentService.validerPaiement(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Permet au client de soumettre les détails de son règlement pour vérification.
     * <p>
     * Cette méthode enregistre le moyen de paiement utilisé, l'identifiant de la transaction 
     * externe et le lien vers la preuve numérique téléversée par le client.
     * </p>
     * * @param paymentId L'identifiant technique du paiement rattaché à la réservation.
     * @param request Le DTO contenant les informations du paiement (méthode, ID transaction, URL).
     * @return Un {@link ResponseEntity} contenant un message de confirmation et le nouveau statut.
     */
    @PostMapping("/{paymentId}/confirmer")
    public ResponseEntity<Map<String, String>> confirmerPaiement(
            @PathVariable("paymentId") Long paymentId, 
            @RequestBody PaymentRequest request) {
        
        paymentService.confirmerPaiement(paymentId, request);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Votre demande a bien été envoyée, un staff se chargera de la validation.");
        response.put("status", "EN_VERIFICATION");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Rejette un paiement (Action réservée au Staff).
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/rejeter")
    public ResponseEntity<Void> rejeterPaiement(@PathVariable(name = "id") Long id) {
        paymentService.rejeterPaiement(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Récupère tous les paiements de l'utilisateur connecté (pour le Dashboard).
     */
    @GetMapping("/me")
    public ResponseEntity<List<PaymentResponse>> getMyPayments() {
        return ResponseEntity.ok(paymentService.getMyPayments());
    }

    /**
     * Récupère tous les paiements liés à une session spécifique (pour le Manager).
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsBySession(@PathVariable(name = "sessionId") Long sessionId) {
        return ResponseEntity.ok(paymentService.getPaymentsBySession(sessionId));
    }
    
    /**
     * Récupère l'historique complet des paiements pour un utilisateur spécifique.
     * Réservé au personnel (ADMIN/MANAGER).
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUser(@PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }
}