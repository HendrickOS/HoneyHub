package fr.honeygroup.repository;

import fr.honeygroup.bo.Circuit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Depot de donnees (Repository) Spring Data JPA dedie a la persistance et a la gestion 
 * de l'entite {@link Circuit}.
 * <p>
 * Ce composant assure l'interface avec la couche de stockage pour les offres touristiques 
 * de type circuit. Il fournit l'ensemble des operations CRUD nécessaires au pilotage 
 * du catalogue des voyages.
 * </p>
 */
@Repository
public interface CircuitRepository extends JpaRepository<Circuit, Long> {
    // Possibilite d'ajouter des requetes specifiques (ex: recherche par duree ou destination)
}