package fr.honeygroup.repository;

import fr.honeygroup.bo.DemandeLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeLeadRepository extends JpaRepository<DemandeLead, Integer> {

    /**
     * Pour le dashboard commercial : voir les leads par état.
     * (ex: "NOUVEAU", "EN_COURS", "CONVERTI")
     */
    List<DemandeLead> findByStatut(String statut);

    /**
     * Pour l'historique d'un client spécifique.
     */
    List<DemandeLead> findByClientId(Integer clientId);

    /**
     * Pour filtrer les demandes par pôle (ex: toutes les demandes "Boxe").
     */
    List<DemandeLead> findByPoleId(Integer poleId);

    /**
     * Bonus : Trouver les demandes les plus récentes en premier.
     */
    List<DemandeLead> findAllByOrderByDateSoumissionDesc();
}