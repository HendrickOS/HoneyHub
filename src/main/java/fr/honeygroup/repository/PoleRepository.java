package fr.honeygroup.repository;

import fr.honeygroup.bo.Pole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Dépôt de données (Repository) Spring Data JPA dédié à la persistance et à la gestion de l'entité {@link Pole}.
 * <p>
 * Ce composant gère l'accès aux données des grands pôles sectoriels de l'entreprise (Écotourisme, IT Outsourcing).
 * Il fournit des méthodes de recherche par unicité nominative, de filtrage textuel et des clauses de tri 
 * natif pour optimiser le rendu des menus sur les applications Frontend.
 * </p>
 */
@Repository
public interface PoleRepository extends JpaRepository<Pole, Long> {

    /**
     * Vérifie de manière optimisée l'existence d'un pôle d'activité via son libellé nominatif exact.
     * <p>
     * <strong>Contrôle d'intégrité :</strong> Exploitée de façon défensive dans la couche métier (BLL) 
     * lors des phases de création ou de modification pour interdire l'insertion de doublons sur le nom du pôle.
     * Génère une requête SQL {@code EXISTS} très performante.
     * </p>
     * * @param nom Le libellé nominatif exact à contrôler (ex: "Écotourisme").
     * @return {@code true} si un pôle porte déjà ce nom, {@code false} autrement.
     */
    boolean existsByNom(String nom);

    /**
     * Recherche et extrait un pôle d'activité à partir de son libellé nominatif exact.
     * <p>
     * Permet de récupérer l'entité complète et son graphe associé lors des requêtes ciblées 
     * par chaînes textuelles plutôt que par clés techniques.
     * </p>
     * * @param nom Le nom exact du pôle recherché.
     * @return Un {@link Optional} contenant l'entité localisée, ou vide.
     */
    Optional<Pole> findByNom(String nom);
    
    /**
     * Extrait l'intégralité des pôles d'activité ordonnés selon le tri alphabétique de leur nom.
     * <p>
     * <strong>Optimisation d'affichage :</strong> Délègue le tri directement au moteur de base de données 
     * MariaDB (clause {@code ORDER BY nom ASC}), garantissant une présentation standardisée, propre 
     * et prévisible des sections au niveau de la barre de navigation du Frontend.
     * </p>
     * * @return Une liste triée de tous les {@link Pole}.
     */
    List<Pole> findAllByOrderByNomAsc();

    /**
     * Recherche floue et insensible à la casse de pôles d'activité par correspondance textuelle partielle.
     * <p>
     * Alimente les suggestions dynamiques ou les barres de recherche globales de la plateforme 
     * en traduisant l'appel par un opérateur SQL {@code LIKE %nom%}.
     * </p>
     * * @param nom Le fragment ou motif textuel saisi par l'opérateur.
     * @return Une liste de {@link Pole} dont le libellé correspond au motif.
     */
    List<Pole> findByNomContainingIgnoreCase(String nom);
}