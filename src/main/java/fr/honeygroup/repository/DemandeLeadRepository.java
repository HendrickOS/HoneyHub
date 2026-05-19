package fr.honeygroup.repository;

import fr.honeygroup.bo.DemandeLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Dépôt de données (Repository) Spring Data JPA dédié à la persistance et à la gestion de l'entité {@link DemandeLead}.
 * <p>
 * Ce composant constitue le cœur du tunnel d'acquisition commerciale de Honey Group. Il permet le suivi, 
 * le filtrage multicritère par statut de workflow ou par pôle d'activité, ainsi que l'extraction chronologique 
 * des opportunités d'affaires pour les tableaux de bord des gestionnaires.
 * </p>
 */
@Repository
public interface DemandeLeadRepository extends JpaRepository<DemandeLead, Long> {

    /**
     * Filtre et extrait les dossiers de prospection en fonction de leur état d'avancement commercial.
     * <p>
     * <strong>Gestion du Pipe Commercial :</strong> Permet d'alimenter les colonnes du tableau de bord 
     * des équipes de vente en isolant les dossiers selon leur phase (ex: "NOUVEAU", "EN_COURS", "CONVERTI").
     * </p>
     * * @param statut Le libellé textuel de l'état recherché.
     * @return Une liste de {@link DemandeLead} partageant le même statut.
     */
    List<DemandeLead> findByStatut(String statut);

    /**
     * Récupère l'historique complet des expressions de besoins soumises par un utilisateur spécifique.
     * <p>
     * Permet de consolider la fiche client ou de restituer l'historique des demandes de contact 
     * directement sur l'espace personnel sécurisé du prospect.
     * </p>
     * * @param userId Identifiant technique unique de l'utilisateur ou du prospect émetteur.
     * @return Une liste de {@link DemandeLead} associées à ce compte.
     */
    List<DemandeLead> findByUserId(Long userId);

    /**
     * Extrait l'intégralité des opportunités d'affaires rattachées à un pôle sectoriel de l'entreprise.
     * <p>
     * Utile pour ventiler les demandes et les affecter aux équipes d'experts concernées, par exemple 
     * pour lister tous les leads du pôle "IT Outsourcing" en attente de chiffrage technique.
     * </p>
     * * @param poleId Identifiant technique unique du pôle d'activité parent.
     * @return Une liste de {@link DemandeLead} affiliées au pôle spécifié.
     */
    List<DemandeLead> findByPoleId(Long poleId);

    /**
     * Extrait l'intégralité des dossiers de prospection ordonnés du plus récent au plus ancien.
     * <p>
     * <strong>Optimisation Opérationnelle :</strong> Délègue le tri chronologique inverse directement au moteur 
     * de base de données MariaDB (clause {@code ORDER BY date_soumission DESC}), garantissant un traitement prioritaire 
     * des demandes de contact les plus fraîches par les équipes commerciales.
     * </p>
     * * @return Une liste chronologique descendante de toutes les {@link DemandeLead}.
     */
    List<DemandeLead> findAllByOrderByDateSoumissionDesc();
    
    List<DemandeLead> findByPrestation_Pole_Id(Long poleId);
}