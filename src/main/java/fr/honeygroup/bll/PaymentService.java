package fr.honeygroup.bll;

import fr.honeygroup.bo.response.PaymentResponse;
import java.util.List;

/**
 * Contrat d'interface définissant la logique métier liée à la gestion des transactions financières.
 * <p>
 * Ce service orchestre le workflow de vérification comptable asynchrone des justificatifs de paiement 
 * (Mobile Money, Virements) et assure l'intégrité du cycle de vie des règlements.
 * </p>
 */
public interface PaymentService {

    /**
     * Récupère le détail exhaustif d'un paiement spécifique identifié par son ID technique.
     */
    PaymentResponse getPaymentDetails(Long paymentId);

    /**
     * Extrait l'historique complet des transactions financières associées à un dossier de réservation.
     */
    List<PaymentResponse> getPaymentsByBooking(Long bookingId);

    /**
     * Valide un paiement après contrôle manuel du justificatif par l'administration.
     * <p>
     * Cette opération déclenche le changement de statut du paiement vers {@code VALIDE} 
     * et peut induire une validation automatique de la réservation liée.
     * </p>
     */
    void validerPaiement(Long paymentId);

    /**
     * Marque un paiement comme rejeté suite à une vérification comptable infructueuse.
     * <p>
     * Ce statut notifie le client de la nécessité de soumettre un nouveau justificatif 
     * conforme aux exigences de Honey Group.
     * </p>
     */
    void rejeterPaiement(Long paymentId);
}