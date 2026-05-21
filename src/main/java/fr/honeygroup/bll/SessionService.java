package fr.honeygroup.bll;

import fr.honeygroup.bo.request.SessionRequest;
import fr.honeygroup.bo.response.SessionResponse;
import fr.honeygroup.enumeration.StatutSession;

/**
 * Contrat de service pour la gestion du cycle de vie et de l'administration des sessions écotouristiques.
 * <p>
 * Ce composant centralise les règles métier applicables aux sessions (création,
 * modification, consultation et gestion des états) pour garantir l'intégrité
 * des données avant leur persistance.
 * </p>
 */
public interface SessionService {

    /**
     * Crée une nouvelle session de voyage à partir des données fournies par un administrateur ou un manager.
     * * @param request Le DTO contenant les informations requises pour la création (dates, capacité, ID prestation).
     * @return Un DTO {@link SessionResponse} représentant la session nouvellement créée et persistée.
     * @throws fr.honeygroup.exception.GlobalExceptionHandler.BusinessLogicException Si la prestation n'existe pas ou si la chronologie des dates est incohérente.
     */
    SessionResponse createSession(SessionRequest request);

    /**
     * Met à jour les informations administratives d'une session existante.
     * * @param id L'identifiant technique unique de la session à modifier.
     * @param request Le DTO contenant les nouvelles valeurs (dates, capacité maximale, statut, etc.).
     * @return Un DTO {@link SessionResponse} reflétant la session après sa mise à jour.
     * @throws fr.honeygroup.exception.GlobalExceptionHandler.BusinessLogicException Si la session n'est pas trouvée, si les dates sont incohérentes, ou si la nouvelle capacité maximale est inférieure au nombre actuel d'inscrits.
     */
    SessionResponse updateSession(Long id, SessionRequest request);

    /**
     * Récupère les détails complets d'une session spécifique.
     * * @param sessionId L'identifiant technique unique de la session.
     * @return Un DTO {@link SessionResponse} contenant les informations de la session.
     * @throws fr.honeygroup.exception.GlobalExceptionHandler.BusinessLogicException Si la session demandée n'existe pas.
     */
    SessionResponse getSessionDetails(Long sessionId);

    /**
     * Effectue une transition d'état sur une session (ex: de OUVERT à COMPLET) après 
     * validation stricte des règles de gestion via l'automate d'états.
     * * @param sessionId L'identifiant technique unique de la session à faire évoluer.
     * @param nouveauStatut Le statut cible souhaité, défini dans {@link StatutSession}.
     * @throws fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException Si la transition demandée est interdite par les règles de l'automate métier.
     * @throws fr.honeygroup.exception.GlobalExceptionHandler.BusinessLogicException Si la session demandée n'existe pas.
     */
    void transitionnerStatut(Long sessionId, StatutSession nouveauStatut);
}