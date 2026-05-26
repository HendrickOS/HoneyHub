package fr.honeygroup.bll.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.honeygroup.bll.PaymentService;
import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.request.PaymentRequest;
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
     * Récupère l'historique de tous les paiements effectués par l'utilisateur connecté.
     * Utilisé pour alimenter le Dashboard client.
     * * @return Liste des paiements appartenant à l'utilisateur authentifié.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPayments() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return paymentRepository.findByBookingUserEmail(userEmail)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
    
    /**
     * Récupère tous les paiements liés à une session spécifique.
     * Accès réservé au personnel (ADMIN/MANAGER).
     * * @param sessionId Identifiant de la session de voyage.
     * @return Liste des paiements rattachés à la session.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsBySession(Long sessionId) {
        return paymentRepository.findByBookingSessionId(sessionId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
    
    /**
     * Récupère l'historique complet des transactions financières pour un utilisateur donné.
     * <p>
     * Cette méthode est destinée au support client et au service comptabilité pour 
     * auditer l'ensemble des règlements effectués par un client spécifique.
     * </p>
     * @param userId Identifiant technique de l'utilisateur cible.
     * @return Liste des paiements associés à l'utilisateur.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUser(Long userId) {
        return paymentRepository.findByBookingUserId(userId)
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
     * Enregistre les détails de la preuve de paiement soumise par le client.
     * <p>
     * Cette méthode valide la transition d'état via l'automate {@link StatutPayment},
     * met à jour les informations de transaction et bascule le paiement dans l'état 
     * {@link StatutPayment#EN_VERIFICATION} pour examen par le personnel.
     * </p>
     * @param paymentId L'identifiant technique du paiement.
     * @param request Le DTO contenant les informations transmises par le client.
     * @throws BusinessSecurityException Si la transition est illégale ou si le paiement est introuvable.
     */
    @Override
    @Transactional
    public void confirmerPaiement(Long paymentId, PaymentRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessLogicException("Paiement introuvable : ID " + paymentId));

        // 1. LEVIER SÉCURITÉ AUTOMATE : Vérification de la transition depuis EN_ATTENTE_PREUVE
        payment.getStatutPaiement().verifierTransition(StatutPayment.EN_VERIFICATION);

        // 2. Mise à jour des données de paiement
        payment.setMethode(request.getMethode());
        payment.setTransactionId(request.getTransactionId());
        payment.setPreuveUrl(request.getPreuveUrl());
        
        // 3. Mutation vers l'état EN_VERIFICATION
        payment.setStatutPaiement(StatutPayment.EN_VERIFICATION);
        
        paymentRepository.save(payment);
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