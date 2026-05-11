package fr.honeygroup.repository;

import fr.honeygroup.bo.Prestation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrestationRepository extends JpaRepository<Prestation, Long> {

    // Filtrer par Pôle (La base de ton catalogue)
    List<Prestation> findByPoleId(Long poleId);

    // Barre de recherche
    List<Prestation> findByTitreServiceContainingIgnoreCase(String titre);
    
    // Pour ton reporting : trouver les prestations les plus chères ou les plus abordables
    List<Prestation> findByPrixBaseBetween(Double min, Double max);
}