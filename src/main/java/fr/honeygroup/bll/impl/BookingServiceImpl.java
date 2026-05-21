package fr.honeygroup.bll.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import enumeration.Role;
import enumeration.StatutBooking;
import fr.honeygroup.bll.BookingService;
import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException;
import fr.honeygroup.exception.GlobalExceptionHandler.SessionCapacityException;
import fr.honeygroup.mapper.BookingMapper;
import fr.honeygroup.repository.BookingRepository;
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

    /**
     * Crée et persiste une nouvelle réservation en base de données pour une session de voyage spécifique.
     * <p>
     * Cette méthode valide la disponibilité des places par rapport à la capacité maximale de la session,
     * calcule dynamiquement le montant financier global et force l'état initial du dossier.
     * </p>
     * * @param request Objet DTO contenant les données d'entrée de la réservation émises par le Frontend.
     * @return BookingResponse Le DTO de réponse enrichi modélisant la réservation persistée.
     * @throws RuntimeException Si l'utilisateur connecté n'est pas identifié, en cas de violation de privilèges (IDOR), 
     * si les entités cibles n'existent pas ou si la jauge maximale de la session est dépassée.
     */
    @Transactional
    public BookingResponse creerReservationSandbox(BookingRequest request) {
        
        // ============================================================================
        // LOGIQUE DE SÉCURITÉ CONTEXTUELLE (Prévention des failles de privilèges / IDOR)
        // ============================================================================
        
        // 1. Extraction de l'identifiant (email) de l'utilisateur actuellement authentifié via Spring Security
        String emailConnecte = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // 2. Récupération de l'entité User correspondante pour valider son existence et auditer son rôle
        User utilisateurConnecte = userRepository.findByEmail(emailConnecte)
                .orElseThrow(() -> new RuntimeException("Erreur de sécurité : L'utilisateur connecté est introuvable en base."));

        // 3. Contrôle de cohérence : On vérifie si l'utilisateur tente de réserver pour le compte d'un autre ID
        if (request.getUserId() != null && !request.getUserId().equals(utilisateurConnecte.getId())) {
            
            // Seuls les membres du personnel authentifiés (ADMIN ou MANAGER) possèdent le droit de substitution
            boolean isStaff = utilisateurConnecte.getRole() == Role.ADMIN || 
                              utilisateurConnecte.getRole() == Role.MANAGER;
            
            if (!isStaff) {
                // CORRECTION ICI : Utilisation de l'exception spécifique pour déclencher le 403
                throw new BusinessSecurityException("Accès refusé : Vos privilèges actuels ne vous permettent pas de réserver pour un tiers.");
            }
        } else {
            // Si le champ userId est omis ou conforme, on force l'injection de l'ID de l'utilisateur authentifié
            request.setUserId(utilisateurConnecte.getId());
        }

        // ============================================================================
        // TRAITEMENT MÉTIER DE LA RÉSERVATION (Modèle 2 : Sessions Fixes)
        // ============================================================================

        // 1. Initialisation de l'entité Booking via le convertisseur de structure (Mapper MapStruct)
        Booking booking = bookingMapper.toEntity(request);

        // 2. Chargement des agrégations et dépendances indispensables depuis les référentiels de données
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Donnée invalide : L'utilisateur ID " + request.getUserId() + " n'existe pas."));
        
        // Récupération de la session temporelle sélective (qui porte le calendrier et la jauge du voyage)
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Donnée invalide : La session de voyage ID " + request.getSessionId() + " est introuvable."));

        // 3. Application de la Règle Métier : Vérification stricte de la jauge de capacité (Demande de Robert)
        int placesDemandees = request.getNbPersonnes() != null ? request.getNbPersonnes() : 1;
        
        if (session.getNbInscrits() + placesDemandees > session.getCapaciteMax()) {
            // CORRECTION ICI : Utilisation de l'exception spécifique pour déclencher le 400
            throw new SessionCapacityException("Opération impossible : La capacité maximale de cette session est atteinte. Places restantes : " 
                    + (session.getCapaciteMax() - session.getNbInscrits()));
        }

        // 4. Mutation des données et mise à jour dynamique du graphe d'objets
        booking.setUser(user);
        booking.setSession(session);
        booking.setNbPlaces(placesDemandees);
        
        // Incrémentation immédiate du compteur d'inscrits de la session parente (Sauvegardé par cascade de persistance)
        session.setNbInscrits(session.getNbInscrits() + placesDemandees);

        // 5. Calcul de l'enveloppe budgétaire : Extraction du tarif unitaire de la prestation via la session
        BigDecimal prixUnitaire = BigDecimal.valueOf(session.getPrestation().getPrixBase());
        BigDecimal total = prixUnitaire.multiply(new BigDecimal(placesDemandees));
        booking.setMontantTotal(total);

        // 6. Cadrage du Workflow Métier : Initialisation obligatoire au statut de blocage "EN_ATTENTE_PAIEMENT"
        // Le client doit ensuite soumettre sa preuve d'upload (Mobile Money / RIB) pour validation administrative
        booking.setStatut(StatutBooking.EN_ATTENTE_PAIEMENT);

        // ============================================================================
        // PERSISTANCE ET RETOUR
        // ============================================================================
        
        // Sauvegarde de l'arbre d'entités en base de données MariaDB
        Booking savedBooking = bookingRepository.save(booking);

        // Conversion de l'entité persistée vers le format de réponse sécurisé destiné au Frontend
        return bookingMapper.toResponse(savedBooking);
    }
    
    /**
     * Extrait l'historique complet des dossiers de réservation appartenant exclusivement à l'utilisateur authentifié.
     * <p>
     * Par mesure de sécurité, aucune variable d'identité externe n'est acceptée en paramètre : 
     * l'évaluation se base de manière hermétique sur le jeton de sécurité de la session courante.
     * </p>
     * * @return Une liste de {@link BookingResponse} ordonnée chronologiquement de la plus récente à la plus ancienne.
     * @throws RuntimeException Si l'identité de l'appelant ne correspond à aucun utilisateur en base.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getUtilisateurHistoriquePersonnel() {
        // Extraction de l'identité de l'appelant
        String emailConnecte = SecurityContextHolder.getContext().getAuthentication().getName();
        
        User user = userRepository.findByEmail(emailConnecte)
                .orElseThrow(() -> new RuntimeException("Utilisateur [" + emailConnecte + "] introuvable en base de données."));

        // Récupération des dossiers triés chronologiquement par date de création décroissante
        return bookingRepository.findByUserIdOrderByDateCreationResaDesc(user.getId())
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }
    
    /**
     * Extrait l'historique d'activité d'un compte client ciblé.
     * <p>
     * Cette méthode de consultation est à accès restreint et doit être protégée en amont par des annotations 
     * de sécurité (ex: PreAuthorize) pour réserver son exécution exclusive aux rôles ADMINISTRATEUR ou MANAGER.
     * </p>
     * * @param userId Identifiant technique de l'utilisateur dont on souhaite auditer les réservations.
     * @return Une liste de {@link BookingResponse} liée au compte client ciblé.
     * @throws RuntimeException Si l'identifiant fourni ne correspond à aucun enregistrement.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getDossierClientPourStaff(Long userId) {
        // Vérification d'existence de la cible pour sécuriser le traitement de la liste
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
     * Un contrôle de sécurité strict est opéré pour vérifier que le demandeur est bien le titulaire légitime du dossier.
     * </p>
     * * @param bookingId Identifiant de la réservation à basculer en attente de résiliation.
     * @throws RuntimeException Si le dossier est introuvable, ou si l'utilisateur connecté tente d'altérer 
     * une réservation tierce (tentative de contournement IDOR).
     */
    @Transactional
    public void demanderAnnulation(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Dossier introuvable : Impossible de localiser la réservation ID " + bookingId));

        // Validation stricte de la propriété du dossier
        String emailConnecte = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!booking.getUser().getEmail().equals(emailConnecte)) {
            // CORRECTION ICI : Utilisation de l'exception spécifique pour déclencher le 403
            throw new BusinessSecurityException("Violation d'accès : Vous n'êtes pas propriétaire de ce dossier de réservation.");
        }

        // Mutation du statut vers l'étape d'examen par le personnel de Honey Group
        booking.setStatut(StatutBooking.DEMANDE_ANNULATION);
        bookingRepository.save(booking);
    }
    
    /**
     * Approuve et valide définitivement la résiliation d'une réservation (Action d'administration).
     * <p>
     * Cette opération clôture le dossier et recalcule instantanément à la baisse la jauge d'occupation 
     * de la session temporelle associée afin de libérer immédiatement les places pour de futurs acheteurs.
     * </p>
     * * @param bookingId Identifiant technique de la réservation à clôturer.
     * @throws RuntimeException Si le dossier de réservation spécifié est introuvable.
     */
    @Transactional
    public void approuverAnnulation(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Dossier introuvable : Impossible de localiser la réservation ID " + bookingId));

        // Logique Métier : Libération des places réservées dans le calendrier pour les prochains clients
        Session session = booking.getSession();
        if (session != null) {
            // Calcul sécurisé pour éviter un compteur d'inscrits négatif en cas d'anomalie
            int nouveauxInscrits = Math.max(0, session.getNbInscrits() - booking.getNbPlaces());
            session.setNbInscrits(nouveauxInscrits);
        }

        // Clôture définitive du dossier
        booking.setStatut(StatutBooking.ANNULE);
        bookingRepository.save(booking);
    }
}