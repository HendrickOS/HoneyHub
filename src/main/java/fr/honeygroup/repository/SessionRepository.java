package fr.honeygroup.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.honeygroup.bo.Session;
import fr.honeygroup.enumeration.StatutSession;

/**
 * Dépôt de données (Repository) Spring Data JPA dédié à la persistance et à la gestion de l'entité {@link Session}.
 * <p>
 * Ce composant orchestre les interactions avec la table PRESTATION_SESSION (ou équivalent),
 * offrant des méthodes de recherche optimisées pour le moteur de réservation écotouristique.
 * </p>
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Extrait l'historique complet de toutes les sessions planifiées pour une prestation spécifique.
     * <p>
     * Cette méthode est principalement utilisée par les modules d'administration pour 
     * l'audit du calendrier global d'une offre, sans filtrage de disponibilité.
     * </p>
     * * @param prestationId Identifiant technique unique de la prestation catalogue.
     * @return Une liste non triée de toutes les sessions associées à l'offre.
     */
    List<Session> findByPrestationId(Long prestationId);

    /**
     * Recherche les sessions éligibles à la réservation pour une prestation donnée.
     * <p>
     * <strong>Optimisation Métier :</strong> Cette requête JPQL applique trois filtres défensifs :
     * <ul>
     * <li>Temporalité : Exclusion des sessions ayant déjà débuté ({@code dateDebut > now}).</li>
     * <li>Disponibilité : Exclusion des sessions dont la jauge est pleine ({@code nbInscrits < capaciteMax}).</li>
     * <li>État : Restriction stricte aux sessions dont le statut est {@code OUVERT}.</li>
     * </ul>
     * Le résultat est automatiquement trié par ordre chronologique pour faciliter l'affichage client.
     * </p>
     * * @param prestationId Identifiant de la prestation cible.
     * @param now          Référentiel temporel actuel.
     * @param statut       Le statut requis pour la réservation (ex: {@link StatutSession#OUVERT}).
     * @return Une liste filtrée et triée de sessions prêtes à être réservées.
     */
    @Query("SELECT s FROM Session s WHERE s.prestation.id = :prestationId " +
           "AND s.dateDebut > :now " +
           "AND s.nbInscrits < s.capaciteMax " +
           "AND s.statutSession = :statut " +
           "ORDER BY s.dateDebut ASC")
    List<Session> findAvailableSessionsByPrestationId(
            @Param("prestationId") Long prestationId, 
            @Param("now") LocalDateTime now,
            @Param("statut") StatutSession statut);
}