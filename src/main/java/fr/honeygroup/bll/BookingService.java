package fr.honeygroup.bll;

import java.util.List;

import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;
import fr.honeygroup.enumeration.StatutBooking;

/**
 * Contrat d'interface définissant la logique métier liée à la gestion des réservations.
 * <p>
 * Ce service centralise les règles d'intégrité financière, la régulation des jauges 
 * d'inscriptions aux sessions fermes et applique des barrières de sécurité (anti-IDOR).
 * </p>
 */
public interface BookingService {

    /**
     * Crée et persiste une nouvelle réservation en base de données pour une session de voyage spécifique.
     */
    BookingResponse creerReservationSandbox(BookingRequest request);

    /**
     * Extrait l'historique complet des dossiers de réservation appartenant exclusivement à l'utilisateur authentifié.
     */
    List<BookingResponse> getUtilisateurHistoriquePersonnel();

    /**
     * Extrait l'historique d'activité d'un compte client ciblé (accès Staff).
     */
    List<BookingResponse> getDossierClientPourStaff(Long userId);

    /**
     * Initialise une procédure de résiliation sur un dossier de réservation.
     */
    void demanderAnnulation(Long bookingId);

    /**
     * Approuve et valide définitivement la résiliation d'une réservation (Action d'administration).
     */
    void approuverAnnulation(Long bookingId);
    
    List<BookingResponse> getBookingsByStatus(StatutBooking status);
}