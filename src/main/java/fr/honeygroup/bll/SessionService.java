package fr.honeygroup.bll;

import fr.honeygroup.bo.response.SessionResponse;
import fr.honeygroup.enumeration.StatutSession;

/**
 * Contrat de service pour la gestion du cycle de vie des sessions écotouristiques.
 */
public interface SessionService {

    /**
     * Récupère les détails d'une session.
     * @param sessionId ID de la session.
     * @return DTO de réponse.
     */
    SessionResponse getSessionDetails(Long sessionId);

    /**
     * Effectue une transition d'état sur une session après vérification métier.
     * @param sessionId ID de la session.
     * @param nouveauStatut Le statut cible souhaité.
     * @throws fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException Si la transition est interdite.
     */
    void transitionnerStatut(Long sessionId, StatutSession nouveauStatut);
}