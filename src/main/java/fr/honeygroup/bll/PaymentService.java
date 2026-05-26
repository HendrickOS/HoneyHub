package fr.honeygroup.bll;

import java.util.List;

import fr.honeygroup.bo.request.PaymentRequest;
import fr.honeygroup.bo.response.PaymentResponse;

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
     * Récupère l'historique de tous les paiements effectués par l'utilisateur actuellement connecté.
     * <p>
     * Cette méthode est utilisée pour alimenter le tableau de bord (Dashboard) client.
     * </p>
     * @return Liste des paiements rattachés à l'utilisateur authentifié.
     */
    List<PaymentResponse> getPaymentsForCurrentUser();

    /**
     * Récupère tous les paiements liés à une session de voyage spécifique.
     * <p>
     * Opération réservée au personnel administratif pour le suivi financier d'une session complète.
     * </p>
     * @param sessionId L'identifiant technique de la session.
     * @return Liste des paiements associés à la session.
     */
    List<PaymentResponse> getPaymentsBySession(Long sessionId);
    
    /**
     * Récupère l'historique de tous les paiements effectués par un utilisateur spécifique.
     * Réservé aux administrateurs.
     */
    List<PaymentResponse> getPaymentsByUser(Long userId);

    /**
     * Valide un paiement après contrôle manuel du justificatif par l'administration.
     * <p>
     * Cette opération déclenche le changement de statut du paiement vers {@code VALIDE} 
     * et peut induire une validation automatique de la réservation liée.
     * </p>
     */
    String validerPaiement(Long paymentId);

    /**
     * Marque un paiement comme rejeté suite à une vérification comptable infructueuse.
     * <p>
     * Ce statut notifie le client de la nécessité de soumettre un nouveau justificatif 
     * conforme aux exigences de Honey Group.
     * </p>
     */
    String rejeterPaiement(Long paymentId);
    
    /**
     * Enregistre les détails du paiement fournis par le client pour permettre la vérification comptable.
     * * @param paymentId L'identifiant technique du paiement.
     * @param request Le DTO contenant les informations de transaction.
     */
    void confirmerPaiement(Long paymentId, PaymentRequest request);
}