package fr.honeygroup.repository;

import fr.honeygroup.bo.CoursLangue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Depot de donnees (Repository) Spring Data JPA dedie a la persistance et a la gestion 
 * de l'entite {@link CoursLangue}.
 * <p>
 * Ce composant permet la manipulation des offres de formation linguistique du pôle 
 * concerné, en offrant les fonctionnalites CRUD standard pour le catalogue de cours.
 * </p>
 */
@Repository
public interface CoursLangueRepository extends JpaRepository<CoursLangue, Long> {
    // Possibilite d'ajouter des methodes de recherche specifiques (ex: findByNiveau)
}