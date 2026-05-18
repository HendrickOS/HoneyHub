package fr.honeygroup.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.honeygroup.bll.PoleService;
import fr.honeygroup.bo.request.PoleRequest;
import fr.honeygroup.bo.response.PoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Contrôleur REST exposant les points de terminaison (endpoints) de l'API pour la gestion des pôles d'activité.
 * <p>
 * Cette classe assure le routage des requêtes HTTP entrantes vers la couche métier (BLL),
 * déclenche la validation automatique des formulaires soumis et expose les ressources 
 * nécessaires aux besoins du Frontend sous l'arborescence standardisée {@code /api/poles}.
 * </p>
 */
@RestController
@RequestMapping("/api/poles")
@RequiredArgsConstructor
@CrossOrigin // Autorise le partage de ressources cross-origin (CORS) pour l'intégration fluide avec le Frontend
public class PoleController {

    private final PoleService poleService;

    // ======================
    // CREATE
    // ======================
    /**
     * Enregistre un nouveau pôle d'activité au sein du système.
     * <p>
     * L'annotation {@code @Valid} intercepte la requête pour exécuter les contraintes de validation de surface 
     * du DTO avant de transmettre les données valides à la couche service.
     * </p>
     * * @param request Objet DTO contenant les informations du pôle à créer (nom, description).
     * @return Le {@link PoleResponse} modélisant la ressource nouvellement créée et persistée.
     */
    @PostMapping
    public PoleResponse create(@Valid @RequestBody PoleRequest request) {
        return poleService.create(request);
    }

    // ======================
    // GET ALL
    // ======================
    /**
     * Récupère la liste exhaustive de l'ensemble des pôles d'activité configurés dans l'application.
     * * @return Une liste de {@link PoleResponse} pour l'affichage global sur l'interface utilisateur.
     */
    @GetMapping
    public List<PoleResponse> getAll() {
        return poleService.getAll();
    }

    // ======================
    // GET BY ID
    // ======================
    /**
     * Récupère les détails d'un pôle d'activité spécifique à partir de son identifiant technique.
     * * @param id Identifiant unique du pôle passé au sein de l'URI.
     * @return Le {@link PoleResponse} correspondant au pôle localisé.
     */
    @GetMapping("/{id}")
    public PoleResponse getById(@PathVariable("id") Long id) {
        return poleService.getById(id);
    }

    // ======================
    // GET BY NOM
    // ======================
    /**
     * Recherche un pôle d'activité par son libellé nominatif via un paramètre de requête HTTP (Query Parameter).
     * <p>
     * Exemple d'appel : {@code GET /api/poles/search?nom=Écotourisme}
     * </p>
     * * @param nom Libellé exact du pôle recherché.
     * @return Le {@link PoleResponse} correspondant au pôle ciblé.
     */
    @GetMapping("/search")
    public PoleResponse getByNom(@RequestParam String nom) {
        return poleService.getByNom(nom);
    }

    // ======================
    // DELETE
    // ======================
    /**
     * Supprime un pôle d'activité du système en fonction de son identifiant unique.
     * <p>
     * Ce point d'accès doit être protégé par des règles de sécurité (ex: rôles d'administration) 
     * afin d'empêcher toute suppression accidentelle ou non autorisée du catalogue.
     * </p>
     * * @param id Identifiant technique du pôle à supprimer.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        poleService.deleteById(id);
    }
}