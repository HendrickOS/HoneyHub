package fr.honeygroup.bll.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.honeygroup.bll.BookingService;
import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;
import fr.honeygroup.enumeration.Role;
import fr.honeygroup.enumeration.StatutBooking;
import fr.honeygroup.enumeration.StatutPayment;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException;
import fr.honeygroup.exception.GlobalExceptionHandler.SessionCapacityException;
import fr.honeygroup.mapper.BookingMapper;
import fr.honeygroup.repository.BookingRepository;
import fr.honeygroup.repository.PaymentRepository;
import fr.honeygroup.repository.SessionRepository;
import fr.honeygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Service métier gérant le cycle de vie, les contrôles d'accès et les workflows 
 * des réservations (Booking) pour le pôle Écotourisme de Honey Group.
 * <p>
 * Ce composant centralise les règles d'intégrité financière (calculs via BigDecimal), 
 * la régulation des jauges d'inscriptions aux sessions fermes et applique des barrières de sécurité 
 * contre les vulnérabilités de type IDOR (Insecure Direct Object Reference).
 * </p>
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final PaymentRepository paymentRepository;

    /**
     * Crée et persiste une nouvelle réservation en base de données pour une session de voyage spécifique.
     * <p>
     * Cette méthode effectue plusieurs opérations critiques :
     * 1. Valide l'identité du demandeur (prévention IDOR).
     * 2. Vérifie la disponibilité des places par rapport à la capacité maximale de la session.
     * 3. Calcule dynamiquement le montant financier global.
     * 4. Initialise le statut du dossier et génère automatiquement un enregistrement de paiement 
     * lié à l'état {@code EN_VERIFICATION}.
     * </p>
     * * @param request Objet DTO contenant les données d'entrée de la réservation.
     * @return BookingResponse Le DTO de réponse enrichi modélisant la réservation persistée.
     * @throws RuntimeException Si l'utilisateur est introuvable ou si les entités cibles sont manquantes.
     * @throws BusinessSecurityException Si une tentative d'usurpation d'identité (IDOR) est détectée.
     * @throws SessionCapacityException Si le nombre de places demandées excède la capacité restante de la session.
     */
    @Override
    @Transactional
    public BookingResponse creerReservationSandbox(BookingRequest request) {
        
        // ============================================================================
        // LOGIQUE DE SÉCURITÉ CONTEXTUELLE (Prévention des failles de privilèges / IDOR)
        // ============================================================================
        
        String emailConnecte = SecurityContextHolder.getContext().getAuthentication().getName();
        
        User utilisateurConnecte = userRepository.findByEmail(emailConnecte)
                .orElseThrow(() -> new RuntimeException("Erreur de sécurité : L'utilisateur connecté est introuvable en base."));

        if (request.getUserId() != null && !request.getUserId().equals(utilisateurConnecte.getId())) {
            boolean isStaff = utilisateurConnecte.getRole() == Role.ADMIN || 
                              utilisateurConnecte.getRole() == Role.MANAGER;
            
            if (!isStaff) {
                throw new BusinessSecurityException("Accès refusé : Vos privilèges actuels ne vous permettent pas de réserver pour un tiers.");
            }
        } else {
            request.setUserId(utilisateurConnecte.getId());
        }

        // ============================================================================
        // TRAITEMENT MÉTIER DE LA RÉSERVATION
        // ============================================================================

        Booking booking = bookingMapper.toEntity(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Donnée invalide : L'utilisateur ID " + request.getUserId() + " n'existe pas."));
        
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Donnée invalide : La session de voyage ID " + request.getSessionId() + " est introuvable."));

        int placesDemandees = request.getNbPersonnes() != null ? request.getNbPersonnes() : 1;
        
        if (session.getNbInscrits() + placesDemandees > session.getCapaciteMax()) {
            throw new SessionCapacityException("Opération impossible : La capacité maximale de cette session est atteinte. Places restantes : " 
                    + (session.getCapaciteMax() - session.getNbInscrits()));
        }

        booking.setUser(user);
        booking.setSession(session);
        booking.setNbPlaces(placesDemandees);
        session.setNbInscrits(session.getNbInscrits() + placesDemandees);

        BigDecimal prixUnitaire = BigDecimal.valueOf(session.getPrestation().getPrixBase());
        BigDecimal total = prixUnitaire.multiply(new BigDecimal(placesDemandees));
        booking.setMontantTotal(total);
        booking.setStatut(StatutBooking.EN_ATTENTE_PAIEMENT);

        // ============================================================================
        // PERSISTANCE ET INITIALISATION DU PAIEMENT
        // ============================================================================
        
        // 1. Sauvegarde du dossier de réservation
        Booking savedBooking = bookingRepository.save(booking);

        // 2. Création automatique de la ligne de paiement corrélée
        Payment initialPayment = new Payment();
        initialPayment.setBooking(savedBooking);
        initialPayment.setStatutPaiement(StatutPayment.EN_ATTENTE_PREUVE);
        initialPayment.setMontantPaye(savedBooking.getMontantTotal());
        
        // On récupère l'instance retournée par le repository pour obtenir son ID généré
        Payment savedPayment = paymentRepository.save(initialPayment);

        // 3. Transformation en réponse et injection manuelle du paymentId
        paymentRepository.save(initialPayment);
        savedBooking.setPayments(List.of(savedPayment));
        
        return bookingMapper.toResponse(savedBooking);
    }
    
    /**
     * Extrait l'historique complet des dossiers de réservation appartenant exclusivement à l'utilisateur authentifié.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getUtilisateurHistoriquePersonnel() {
        String emailConnecte = SecurityContextHolder.getContext().getAuthentication().getName();
        
        User user = userRepository.findByEmail(emailConnecte)
                .orElseThrow(() -> new RuntimeException("Utilisateur [" + emailConnecte + "] introuvable en base de données."));

        return bookingRepository.findByUserIdOrderByDateCreationResaDesc(user.getId())
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }
    
    /**
     * Extrait l'historique d'activité d'un compte client ciblé.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getDossierClientPourStaff(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Requête invalide : L'utilisateur ciblé avec l'ID " + userId + " n'existe pas.");
        }

        return bookingRepository.findByUserIdOrderByDateCreationResaDesc(userId)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }
    
    /**
     * Initialise une procédure de résiliation sur un dossier de réservation en mutant son statut d'avancement.
     * <p>
     * Cette méthode vérifie l'identité du propriétaire du dossier et valide la légitimité de 
     * la transition d'état via le moteur de workflow embarqué dans l'énumération {@link StatutBooking}.
     * </p>
     * @param bookingId Identifiant unique de la réservation à annuler.
     * @throws BusinessSecurityException Si le demandeur n'est pas propriétaire ou si la transition 
     * d'état est interdite par la machine à états.
     */
    @Override
    @Transactional
    public void demanderAnnulation(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Dossier introuvable : Impossible de localiser la réservation ID " + bookingId));

        // 1. Validation stricte de la propriété du dossier (Prévention IDOR)
        String emailConnecte = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!booking.getUser().getEmail().equals(emailConnecte)) {
            throw new BusinessSecurityException("Violation d'accès : Vous n'êtes pas propriétaire de ce dossier de réservation.");
        }

        // 2. LEVIER SÉCURITÉ AUTOMATE : Vérification de la conformité de la transition d'état
        booking.getStatut().verifierTransition(StatutBooking.DEMANDE_ANNULATION);

        // 3. Mutation du statut vers l'étape d'examen
        booking.setStatut(StatutBooking.DEMANDE_ANNULATION);
        bookingRepository.save(booking);
    }
    
    /**
     * Approuve et valide définitivement la résiliation d'une réservation (Action d'administration).
     * <p>
     * Cette méthode valide la légitimité de la transition d'état via la machine à états 
     * intégrée à {@link StatutBooking}, puis procède à la libération des ressources 
     * (places en session) libérées par l'annulation.
     * </p>
     * @param bookingId Identifiant unique de la réservation à clôturer.
     * @throws BusinessSecurityException Si la transition d'état demandée est illégale selon le workflow métier.
     */
    @Override
    @Transactional
    public void approuverAnnulation(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Dossier introuvable : Impossible de localiser la réservation ID " + bookingId));

        // 1. LEVIER SÉCURITÉ AUTOMATE : Validation réglementaire via le moteur de workflow
        booking.getStatut().verifierTransition(StatutBooking.ANNULE);

        // 2. Logique Métier : Libération des places réservées dans le calendrier pour les prochains clients
        Session session = booking.getSession();
        if (session != null) {
            int nouveauxInscrits = Math.max(0, session.getNbInscrits() - booking.getNbPlaces());
            session.setNbInscrits(nouveauxInscrits);
        }

        // 3. Clôture définitive du dossier
        booking.setStatut(StatutBooking.ANNULE);
        bookingRepository.save(booking);
    }
    
    @Override
    public List<BookingResponse> getBookingsByStatus(StatutBooking status) {
        return bookingRepository.findByStatut(status).stream()
                .map(bookingMapper::toResponse) // En supposant que tu aies un BookingMapper
                .toList();
    }
}