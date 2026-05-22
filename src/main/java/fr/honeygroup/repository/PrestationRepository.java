package fr.honeygroup.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.honeygroup.bo.Prestation;

/**
 * Dépôt de données (Repository) Spring Data JPA dédié à la persistance et à la gestion de l'entité {@link Prestation}.
 * <p>
 * Ce composant constitue la colonne vertébrale du catalogue d'offres de Honey Group. Il permet le requêtage 
 * multicritère des services (circuits écotouristiques ou offres d'ingénierie IT), leur filtrage par pôle d'activité 
 * et l'extraction de segments tarifaires pour les fonctionnalités de recherche avancée.
 * </p>
 */
@Repository
public interface PrestationRepository extends JpaRepository<Prestation, Long> {

    /**
     * Extrait l'ensemble des prestations rattachées à un pôle d'activité sectoriel spécifique.
     * <p>
     * <strong>Pivot de l'affichage Catalogue :</strong> Cette méthode est exploitée par le Frontend pour cloisonner 
     * et afficher dynamiquement soit les séjours de l'Écotourisme, soit les forfaits de l'IT Outsourcing 
     * sur leurs espaces respectifs.
     * </p>
     * * @param poleId Identifiant technique unique du pôle parent (ex: 1 pour Écotourisme).
     * @return Une liste de {@link Prestation} affiliées au pôle ciblé.
     */
    List<Prestation> findByPoleId(Long poleId);

    /**
     * Effectue une recherche textuelle floue sur l'intitulé commercial de la prestation.
     * <p>
     * Alimente le composant de barre de recherche (Search Bar) global du site Web. L'utilisation 
     * de {@code ContainingIgnoreCase} se traduit en SQL par une clause {@code LIKE %titre%} 
     * insensible aux variations de majuscules et minuscules.
     * </p>
     * * @param titre La chaîne de caractères ou le mot-clé saisi par l'utilisateur.
     * @return Une liste de {@link Prestation} dont le titre correspond partiellement au motif.
     */
    List<Prestation> findByTitreServiceContainingIgnoreCase(String titre);
    
    /**
     * Filtre et extrait les prestations comprises dans une fourchette budgétaire stricte.
     * <p>
     * Utile pour la mise en place de filtres par curseurs de prix (Price Sliders) sur le catalogue Frontend, 
     * ou pour l'extraction d'analyses statistiques financières par le Staff de direction.
     * </p>
     * * @param min Borne tarifaire inférieure de la recherche.
     * @param max Borne tarifaire supérieure de la recherche.
     * @return Une liste de {@link Prestation} s'alignant dans la tranche financière spécifiée.
     */
    List<Prestation> findByPrixBaseBetween(BigDecimal min, BigDecimal max);
    
    /**
     * Recherche les prestations dont le lieu de départ (stocké dans le JSON metadata) 
     * correspond à la valeur fournie.
     * <p>
     * Utilise le chemin JSON pour extraire la valeur "lieu_depart".
     * </p>
     * * @param lieuDepart Le nom du lieu de départ recherché.
     * @return Une liste de prestations correspondant au critère.
     */
    @Query(value = "SELECT * FROM PRESTATION WHERE metadata->>'$.lieu_depart' = :lieu", nativeQuery = true)
    List<Prestation> findByLieuDepart(@Param("lieu") String lieuDepart);
    
    /**
     * Recherche les prestations dont la destination (lieu d'arrivée) 
     * correspond à la valeur fournie dans les meta-données JSON.
     * * @param lieuArrivee La destination recherchée.
     * @return Une liste de prestations correspondant au critère.
     */
    @Query(value = "SELECT * FROM PRESTATION WHERE metadata->>'$.lieu_arrivee' = :lieu", nativeQuery = true)
    List<Prestation> findByLieuArrivee(@Param("lieu") String lieuArrivee);
    
    /**
     * Filtre les prestations selon un itinéraire géographique précis.
     * <p>
     * Cette requête native interroge simultanément les deux clés JSON pour restreindre
     * le résultat aux offres correspondant exactement au trajet souhaité.
     * </p>
     * @param depart Le point de départ.
     * @param arrivee Le point d'arrivée (destination).
     * @return Une liste de {@link Prestation} correspondant au trajet complet.
     */
    @Query(value = "SELECT * FROM PRESTATION WHERE metadata->>'$.lieu_depart' = :depart AND metadata->>'$.lieu_arrivee' = :arrivee", nativeQuery = true)
    List<Prestation> findByTrajet(@Param("depart") String depart, @Param("arrivee") String arrivee);
}