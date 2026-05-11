package fr.honeygroup.repository;

import fr.honeygroup.bo.DetailsSpecifiques;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailsSpecifiquesRepository extends JpaRepository<DetailsSpecifiques, Long> {

    /**
     * Récupère tous les détails liés à une demande spécifique.
     * Utile si tu veux recharger uniquement les détails sans recharger tout le Lead.
     */
    List<DetailsSpecifiques> findByDemandeLeadId(Long demandeLeadId);

    /**
     * Pour des statistiques : trouver toutes les réponses pour une clé précise.
     * (ex: toutes les valeurs pour la clé "taille_gants")
     */
    List<DetailsSpecifiques> findByChampCle(String champCle);
}