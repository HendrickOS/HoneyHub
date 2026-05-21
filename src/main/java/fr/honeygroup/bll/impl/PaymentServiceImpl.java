package fr.honeygroup.bll.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.honeygroup.bll.PaymentService;
import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.response.PaymentResponse;
import fr.honeygroup.enumeration.StatutBooking;
import fr.honeygroup.enumeration.StatutPayment;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessLogicException;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException;
import fr.honeygroup.mapper.PaymentMapper;
import fr.honeygroup.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

/**
 * Service métier gérant le cycle de vie des transactions financières (Payment) 
 * pour le pôle Écotourisme de Honey Group.
 * <p>
 * Ce composant garantit la traçabilité des règlements et sécurise les transitions 
 * de statuts comptables (Workflow de validation/rejet) en s'appuyant sur l'automate d'états de l'énumération StatutPayment.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    /**
     * Récupère le détail exhaustif d'un paiement spécifique identifié par son ID technique.
     * * @param paymentId Identifiant technique du paiement.
     * @return Le DTO de réponse correspondant au paiement.
     * @throws RuntimeException Si le paiement n'est pas trouvé en base de données.
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentDetails(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(paymentMapper::toResponse)
                .orElseThrow(() -> new BusinessLogicException("Paiement introuvable : ID " + paymentId));
    }

    /**
     * Extrait l'historique complet des transactions financières associées à un dossier de réservation.
     * * @param bookingId Identifiant de la réservation cible.
     * @return Une liste de {@link PaymentResponse} liées au dossier.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    /**
     * Valide un paiement et synchronise automatiquement le statut de la réservation associée.
     * <p>
     * Cette méthode garantit que le workflow financier est cohérent avec le workflow opérationnel :
     * 1. Vérification de la transition d'état via l'automate de l'énumération.
     * 2. Mise à jour du statut du paiement vers {@code VALIDE}.
     * 3. Basculement automatique de la réservation liée vers le statut {@code CONFIRME}.
     * </p>
     * @param paymentId Identifiant technique du paiement.
     * @throws BusinessSecurityException Si la transition est illégale ou si la réservation associée est introuvable.
     */
    @Override
    @Transactional
    public void validerPaiement(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessLogicException("Paiement introuvable : ID " + paymentId));

        // 1. Vérification de la transition (Automate d'état)
        if (!payment.getStatutPaiement().peutBasculerVers(StatutPayment.VALIDE)) {
            throw new BusinessSecurityException("Action refusée : Transition illégale depuis le statut actuel (" + payment.getStatutPaiement() + ").");
        }

        // 2. Mise à jour du paiement
        payment.setStatutPaiement(StatutPayment.VALIDE);
        paymentRepository.save(payment);

        // 3. Mise à jour du Booking associé (Workflow croisé)
        if (payment.getBooking() != null) {
            payment.getBooking().setStatut(StatutBooking.CONFIRME);
            // La sauvegarde est gérée par la cascade ou par le contexte de persistance @Transactional
        }
    }

    /**
     * Marque un paiement comme rejeté suite à une vérification comptable infructueuse.
     * <p>
     * Le dossier de réservation reste dans son état actuel (attente de paiement) pour permettre
     * au client de soumettre une nouvelle preuve de transaction conforme.
     * </p>
     * @param paymentId Identifiant technique du paiement à rejeter.
     * @throws BusinessSecurityException Si la transition d'état vers {@code REJETE} est illégale.
     */
    @Override
    @Transactional
    public void rejeterPaiement(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessLogicException("Paiement introuvable : ID " + paymentId));

        // 1. Vérification de la transition via l'automate de l'énumération
        if (!payment.getStatutPaiement().peutBasculerVers(StatutPayment.REJETE)) {
            throw new BusinessSecurityException("Action refusée : Transition illégale depuis le statut actuel (" + payment.getStatutPaiement() + ").");
        }

        // 2. Mise à jour du statut du paiement vers REJETE
        payment.setStatutPaiement(StatutPayment.REJETE);
        paymentRepository.save(payment);
        
        // Note : Le Booking n'est pas modifié ici, il reste en EN_ATTENTE_PAIEMENT,
        // ce qui permet au client de charger un nouveau justificatif via l'interface.
    }
}