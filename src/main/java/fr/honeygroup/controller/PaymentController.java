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
     * Valide un paiement soumis par un client et confirme la réservation associée.
     * <p>
     * Cette opération est réservée aux utilisateurs disposant des rôles ADMIN ou MANAGER.
     * Elle effectue une transition atomique du paiement vers l'état VALIDE et de la 
     * réservation vers l'état CONFIRME.
     * </p>
     * * @param id L'identifiant technique du paiement à valider.
     * @return Un {@link ResponseEntity} contenant un message de confirmation et les nouveaux statuts.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/valider")
    public ResponseEntity<Map<String, String>> validerPaiement(@PathVariable(name = "id") Long id) {
        paymentService.validerPaiement(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Le paiement #" + id + " a été validé avec succès.");
        response.put("status", "VALIDE");
        response.put("bookingStatus", "CONFIRME");
        
        return ResponseEntity.ok(response);
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
     * Marque un paiement comme rejeté suite à une vérification comptable infructueuse.
     * <p>
     * Le dossier de réservation reste dans son état actuel (attente de paiement) pour permettre
     * au client de soumettre une nouvelle preuve de transaction conforme.
     * </p>
     * @param id L'identifiant technique du paiement à rejeter.
     * @return Un {@link ResponseEntity} contenant un message d'information et le statut REJETE.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/rejeter")
    public ResponseEntity<Map<String, String>> rejeterPaiement(@PathVariable(name = "id") Long id) {
        String message = paymentService.rejeterPaiement(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("status", "REJETE");
        
        return ResponseEntity.ok(response);
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