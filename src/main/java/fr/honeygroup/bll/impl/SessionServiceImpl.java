package fr.honeygroup.bll.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.honeygroup.bll.SessionService;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.request.SessionRequest;
import fr.honeygroup.bo.response.SessionResponse;
import fr.honeygroup.enumeration.StatutSession;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessLogicException;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException;
import fr.honeygroup.exception.GlobalExceptionHandler.SessionCapacityException;
import fr.honeygroup.mapper.SessionMapper;
import fr.honeygroup.repository.PrestationRepository;
import fr.honeygroup.repository.SessionRepository;
import lombok.RequiredArgsConstructor;

/**
 * Service métier dédié à la gestion opérationnelle et administrative des sessions écotouristiques.
 * <p>
 * Ce composant orchestre le cycle de vie des sessions (de la création à la clôture comptable)
 * et garantit la conformité des transitions d'états vis-à-vis du workflow métier défini 
 * dans l'énumération {@link StatutSession}. Il assure également le cloisonnement des jauges 
 * de capacité et de la cohérence chronologique des séjours.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final PrestationRepository prestationRepository;
    private final SessionMapper sessionMapper;

    /**
     * Crée et persiste une nouvelle session temporelle rattachée à une prestation catalogue.
     * <p>
     * Cette opération valide l'existence de la prestation parente, la cohérence 
     * chronologique des dates fournies, ainsi que l'impossibilité de planifier un départ 
     * dans le passé, avant d'insérer la session avec le statut par défaut {@code OUVERT}.
     * </p>
     * @param request Le DTO contenant les informations d'initialisation de la session.
     * @return Le DTO de réponse {@link SessionResponse} matérialisant la ressource créée.
     * @throws BusinessLogicException Si la prestation associée est introuvable, si la date de fin précède la date de début, ou si la date de début se situe dans le passé.
     */
    @Override
    @Transactional
    public SessionResponse createSession(SessionRequest request) {
        // 1. Validation de la chronologie des dates (Fin après Début)
        validerChronologieDates(request.getDateDebut(), request.getDateFin());

        // Sécurité temporelle : Interdiction de planifier un départ dans le passé
        if (request.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new BusinessLogicException(
                "Incohérence temporelle : Impossible de créer une session avec une date de départ dans le passé."
            );
        }

        // 2. Récupération de la prestation parente obligatoire
        Prestation prestation = prestationRepository.findById(request.getPrestationId())
                .orElseThrow(() -> new BusinessLogicException("Prestation introuvable : ID " + request.getPrestationId()));

        // 3. Transformation et enrichissement de l'entité
        Session session = sessionMapper.toEntity(request);
        session.setPrestation(prestation);

        // 4. Persistance et retour
        Session sessionSauvegardee = sessionRepository.save(session);
        return sessionMapper.toResponse(sessionSauvegardee);
    }

    /**
     * Met à jour les paramètres structurels d'une session existante (dates, capacité, rattachement).
     * <p>
     * Avant de sauvegarder, la méthode vérifie que la nouvelle jauge de capacité maximale 
     * n'entre pas en conflit avec le nombre de participants ayant déjà réservé sur ce créneau.
     * </p>
     * * @param id L'identifiant technique unique de la session à modifier.
     * @param request Le DTO portant les nouvelles valeurs à appliquer.
     * @return Le DTO de réponse {@link SessionResponse} actualisé.
     * @throws BusinessLogicException Si la session ou la nouvelle prestation cible n'existe pas, ou en cas d'incohérence de dates.
     * @throws SessionCapacityException Si la nouvelle capacité maximale est inférieure au nombre actuel d'inscrits.
     */
    @Override
    @Transactional
    public SessionResponse updateSession(Long id, SessionRequest request) {
        // 1. Récupération de la session existante
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException("Session introuvable : ID " + id));

        // 2. Validation de la chronologie des dates
        validerChronologieDates(request.getDateDebut(), request.getDateFin());

        // 3. Validation de la jauge réglementaire de capacité
        if (request.getCapaciteMax() < session.getNbInscrits()) {
            throw new SessionCapacityException(
                "Ajustement impossible : La nouvelle capacité maximale (" + request.getCapaciteMax() + 
                ") ne peut pas être inférieure au nombre de participants déjà inscrits (" + session.getNbInscrits() + ")."
            );
        }

        // 4. Mise à jour de la prestation si le lien a été modifié
        if (!session.getPrestation().getId().equals(request.getPrestationId())) {
            Prestation nouvellePrestation = prestationRepository.findById(request.getPrestationId())
                    .orElseThrow(() -> new BusinessLogicException("Prestation introuvable : ID " + request.getPrestationId()));
            session.setPrestation(nouvellePrestation);
        }

        // 5. Mutation des champs primitifs
        session.setDateDebut(request.getDateDebut());
        session.setDateFin(request.getDateFin());
        session.setCapaciteMax(request.getCapaciteMax());
        
        if (request.getStatut() != null) {
            session.setStatutSession(request.getStatut());
        }

        // 6. Sauvegarde et conversion du résultat
        Session sessionMiseAJour = sessionRepository.save(session);
        return sessionMapper.toResponse(sessionMiseAJour);
    }

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

    /**
     * Utilitaire de validation sémantique des dates de session.
     * * @param debut Date de début à analyser.
     * @param fin Date de fin à analyser.
     * @throws BusinessLogicException Si la chronologie est inversée ou si une des dates est manquante.
     */
    private void validerChronologieDates(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null) {
            throw new BusinessLogicException("Traitement impossible : Les dates de début et de fin sont obligatoires.");
        }
        if (fin.isBefore(debut)) {
            throw new BusinessLogicException("Incohérence temporelle : La date de fin de la session doit être postérieure à sa date de début.");
        }
    }
}