package fr.honeygroup.bll.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.honeygroup.bll.SessionService;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.response.SessionResponse;
import fr.honeygroup.enumeration.StatutSession;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessLogicException;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException;
import fr.honeygroup.mapper.SessionMapper;
import fr.honeygroup.repository.SessionRepository;
import lombok.RequiredArgsConstructor;

/**
 * Service métier dédié à la gestion opérationnelle des sessions écotouristiques.
 * <p>
 * Ce composant orchestre le cycle de vie des sessions (du statut {@code OUVERT} à la 
 * {@code CLOTURE}) et garantit la conformité des transitions d'états vis-à-vis 
 * du workflow métier défini dans l'énumération {@link StatutSession}.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    /**
     * Récupère le détail exhaustif d'une session identifiée par son ID technique.
     * * @param sessionId Identifiant technique de la session cible.
     * @return Le DTO de réponse {@link SessionResponse} correspondant.
     * @throws BusinessLogicException Si aucune session n'est trouvée avec cet identifiant.
     */
    @Override
    @Transactional(readOnly = true)
    public SessionResponse getSessionDetails(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .map(sessionMapper::toResponse)
                .orElseThrow(() -> new BusinessLogicException("Session introuvable : ID " + sessionId));
    }

    /**
     * Pilote le changement de statut d'une session en s'appuyant sur l'automate d'états métier.
     * <p>
     * Cette méthode effectue deux vérifications critiques :
     * 1. Existence de la ressource en base de données.
     * 2. Légalité de la transition selon le workflow défini dans {@link StatutSession#peutBasculerVers(StatutSession)}.
     * </p>
     * * @param sessionId Identifiant technique de la session à modifier.
     * @param nouveauStatut Le statut de destination souhaité.
     * @throws BusinessLogicException Si la session n'est pas trouvée.
     * @throws BusinessSecurityException Si la transition est interdite par les règles métiers actuelles.
     */
    @Override
    @Transactional
    public void transitionnerStatut(Long sessionId, StatutSession nouveauStatut) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessLogicException("Session introuvable : ID " + sessionId));

        // Vérification de la légitimité du changement d'état via l'automate d'états
        if (!session.getStatutSession().peutBasculerVers(nouveauStatut)) {
            throw new BusinessSecurityException(
                "Action refusée : Transition de statut illégale (" + session.getStatutSession() + 
                " -> " + nouveauStatut + ") pour la session ID " + sessionId
            );
        }

        // Mise à jour et persistance
        session.setStatutSession(nouveauStatut);
        sessionRepository.save(session);
    }
}