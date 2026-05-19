package fr.honeygroup.repository;

import fr.honeygroup.bo.DemandeLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Depot de donnees (Repository) Spring Data JPA dedie a la persistance et a la gestion de l'entite {@link DemandeLead}.
 * <p>
 * Ce composant constitue le coeur du tunnel d'acquisition commerciale de Honey Group. Il permet le suivi, 
 * le filtrage multicritere par statut de workflow ou par pole d'activite, ainsi que l'extraction 
 * chronologique des opportunites d'affaires pour les tableaux de bord des gestionnaires.
 * </p>
 */
@Repository
public interface DemandeLeadRepository extends JpaRepository<DemandeLead, Long> {

    /**
     * Filtre et extrait les dossiers de prospection en fonction de leur etat d'avancement commercial.
     * <p>
     * Gestion du Pipe Commercial : Permet d'alimenter les colonnes du tableau de bord 
     * des equipes de vente en isolant les dossiers selon leur phase (ex: "NOUVEAU", "EN_COURS", "CONVERTI").
     * </p>
     * @param statut Le libelle textuel de l'etat recherche.
     * @return Une liste de {@link DemandeLead} partageant le meme statut.
     */
    List<DemandeLead> findByStatut(String statut);

    /**
     * Recupere l'historique complet des expressions de besoins soumises par un utilisateur specifique.
     * <p>
     * Permet de consolider la fiche client ou de restituer l'historique des demandes de contact 
     * directement sur l'espace personnel securise du prospect.
     * </p>
     * @param userId Identifiant technique unique de l'utilisateur ou du prospect emetteur.
     * @return Une liste de {@link DemandeLead} associees a ce compte.
     */
    List<DemandeLead> findByUserId(Long userId);

    /**
     * Extrait l'integralite des opportunites d'affaires rattachees a un pole sectoriel de l'entreprise.
     * <p>
     * Utile pour ventiler les demandes et les affecter aux equipes d'experts concernees, 
     * par exemple pour lister tous les leads du pole "IT Outsourcing" en attente de chiffrage technique.
     * </p>
     * @param poleId Identifiant technique unique du pole d'activite parent.
     * @return Une liste de {@link DemandeLead} affiliees au pole specifie.
     */
    List<DemandeLead> findByPoleId(Long poleId);

    /**
     * Extrait l'integralite des dossiers de prospection ordonnes du plus recent au plus ancien.
     * <p>
     * Optimisation Operationnelle : Delegue le tri chronologique inverse directement au moteur 
     * de base de donnees, garantissant un traitement prioritaire des demandes les plus fraiches.
     * </p>
     * @return Une liste chronologique descendante de toutes les {@link DemandeLead}.
     */
    List<DemandeLead> findAllByOrderByDateSoumissionDesc();
    
    /**
     * Extrait les demandes de leads liees a une prestation identifiee par son pole de rattachement.
     * <p>
     * Utilise le parcours de relations JPA (Prestation -> Pole) pour filtrer les leads.
     * </p>
     * @param poleId L'identifiant technique du pole.
     * @return Une liste de {@link DemandeLead} filtrees par le pole de la prestation.
     */
    List<DemandeLead> findByPrestation_Pole_Id(Long poleId);
}