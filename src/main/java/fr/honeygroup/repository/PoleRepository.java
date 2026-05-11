package fr.honeygroup.repository;

import fr.honeygroup.bo.Pole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PoleRepository extends JpaRepository<Pole, Long> { // On garde Integer

    // Vérification rapide (SQL EXISTS)
    boolean existsByNom(String nom);

    // Recherche pour récupération
    Optional<Pole> findByNom(String nom);
    
    // Affichage trié pour le Front-end
    List<Pole> findAllByOrderByNomAsc();

    // Recherche flexible (Bonus pour ta barre de recherche)
    List<Pole> findByNomContainingIgnoreCase(String nom);
}