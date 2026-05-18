package fr.honeygroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import fr.honeygroup.bo.Photo;

import java.util.List;

/**
 * Dépôt de données (Repository) Spring Data JPA dédié à la persistance et à la gestion de l'entité {@link Photo}.
 * <p>
 * Ce composant administre le stockage des métadonnées et des chemins d'accès (URLs) 
 * des illustrations multimédias valorisant visuellement le catalogue d'offres de Honey Group.
 * </p>
 */
@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    /**
     * Récupère l'illustration associée à une prestation spécifique.
     * <p>
     * <strong>Affichage Fiche Produit :</strong> Extrait la photo rattachée directement 
     * à la prestation demandée par le client.
     * </p>
     * @param prestationId Identifiant technique unique de la prestation concernée.
     * @return Une liste contenant la {@link Photo} de la prestation (sous forme de liste pour respecter la signature).
     */
    @Query("SELECT pr.photo FROM Prestation pr WHERE pr.id = :prestationId AND pr.photo IS NOT NULL")
    List<Photo> findByPrestation_Id(@Param("prestationId") Long prestationId);

    /**
     * Récupère l'ensemble des photographies illustrant les prestations d'un pôle d'activité global.
     * <p>
     * <strong>Routage Relationnel :</strong> Extrait toutes les photos uniques affectées 
     * aux différentes prestations d'un même secteur (ex: toutes les photos du pôle Écotourisme).
     * </p>
     * @param poleId Identifiant technique unique du pôle macroscopique ciblé.
     * @return Une liste de {@link Photo} liées aux prestations du pôle spécifié.
     */
    @Query("SELECT DISTINCT pr.photo FROM Prestation pr WHERE pr.pole.id = :poleId AND pr.photo IS NOT NULL")
    List<Photo> findByPrestation_Pole_Id(@Param("poleId") Long poleId);
}