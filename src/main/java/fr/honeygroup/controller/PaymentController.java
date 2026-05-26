package fr.honeygroup.controller;

import fr.honeygroup.bll.PaymentService;
import fr.honeygroup.bo.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * Rejette un paiement (Action réservée au Staff).
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/rejeter")
    public ResponseEntity<Void> rejeterPaiement(@PathVariable(name = "id") Long id) {
        paymentService.rejeterPaiement(id);
        return ResponseEntity.ok().build();
    }
}