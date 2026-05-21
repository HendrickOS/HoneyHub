package fr.honeygroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import fr.honeygroup.bo.Session;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dépôt de données (Repository) Spring Data JPA dédié à la persistance et à la gestion de l'entité {@link Session}.
 * <p>
 * Ce composant gère le cycle de vie des sessions temporelles fixes (dates de départ/retour, jauge d'inscrits) 
 * rattachées aux prestations écotouristiques de Honey Group.
 * </p>
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Extrait l'historique complet de toutes les sessions planifiées pour une prestation spécifique.
     * <p>
     * Principalement exploitée par les tableaux de bord d'administration (Staff) pour auditer 
     * le calendrier global d'une offre commerciale, sans distinction de date ou de remplissage.
     * </p>
     * * @param prestationId Identifiant technique unique de la prestation catalogue associée.
     * @return Une liste de {@link Session} rattachées à cette offre.
     */
    List<Session> findByPrestationId(Long prestationId);

    /**
     * Recherche les sessions ouvertes à la réservation pour une prestation donnée (Calendrier Client).
     * <p>
     * <strong>Optimisation Métier :</strong> Cette requête personnalisée (JPQL) filtre de manière défensive 
     * pour ne retourner que les sessions futures (dont la date de début n'est pas dépassée) 
     * et dont la jauge d'inscription en base de données n'a pas atteint le plafond critique de capacité maximale.
     * </p>
     * * @param prestationId Identifiant technique unique de la prestation écotouristique.
     * @param now Horodatage de référence (généralement {@code LocalDateTime.now()}) pour acter le filtrage temporel.
     * @return Une liste filtrée de {@link Session} éligibles à une nouvelle contractualisation.
     */
    @Query("SELECT s FROM Session s WHERE s.prestation.id = :prestationId " +
    	       "AND s.dateDebut > :now " +
    	       "AND s.nbInscrits < s.capaciteMax " +
    	       "AND s.statutSession = 'OUVERT'") // Filtre explicite sur le statut
    List<Session> findAvailableSessionsByPrestationId(@Param("prestationId") Long prestationId, 
                                                      @Param("now") LocalDateTime now);
}