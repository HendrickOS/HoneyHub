package fr.honeygroup.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.honeygroup.bll.PrestationService;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.request.CircuitRequest;
import fr.honeygroup.bo.request.CoursLangueRequest;
import fr.honeygroup.bo.request.PrestationRequest;
import fr.honeygroup.bo.response.PrestationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controleur REST exposant les endpoints de gestion du catalogue des prestations.
 * <p>
 * Cette classe orchestre les flux d'exposition et de modification des offres commerciales 
 * de Honey Group (Prestations generiques, Circuits touristiques et Cours de langues).
 * </p>
 * <p>
 * Les regles de securite associees (configurees dans SecurityConfig) autorisent l'acces 
 * public en lecture (GET) et restreignent les ecritures (POST, DELETE) au role ADMIN.
 * </p>
 */
@RestController
@RequestMapping("/api/prestations")
@RequiredArgsConstructor
@CrossOrigin
public class PrestationController {

    /** Service metier encapsulant la logique de traitement des prestations. */
    private final PrestationService prestationService;
    private final fr.honeygroup.repository.PrestationRepository prestationRepository;
    private final fr.honeygroup.mapper.PrestationMapper prestationMapper;

    /**
     * Recupere l'integralite du catalogue des prestations (generiques et specifiques).
     * <p>
     * Acces public. Utile pour l'affichage initial sur le portail vitrine Front-end.
     * </p>
     * * @return Une ResponseEntity contenant la liste des DTOs PrestationResponse et un code 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<PrestationResponse>> getAllPrestations() {
        return ResponseEntity.ok(prestationService.getAllPrestations());
    }

    /**
     * Recupere le detail textuel et financier d'une prestation specifique via son identifiant unique.
     * <p>
     * Acces public. Exploite par le Front-end lors du clic sur une fiche produit.
     * </p>
     * * @param id L'identifiant unique de la prestation recherchee.
     * @return Une ResponseEntity contenant le DTO PrestationResponse correspondant et un code 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PrestationResponse> getPrestationById(@PathVariable Long id) {
        return ResponseEntity.ok(prestationService.getPrestationById(id));
    }

    /**
     * Cree une nouvelle prestation generique (sans specificites de circuit ou de langue).
     * <p>
     * Acces restreint. Reserve a l'utilisateur possedant le role ADMIN.
     * </p>
     * * @param request Le DTO de requete contenant les attributs de base valides par Jakarta Bean Validation.
     * @return Une ResponseEntity contenant le DTO de la prestation creee et un code 200 OK.
     */
    @PostMapping("/generique")
    public ResponseEntity<PrestationResponse> createPrestationGenerique(@Valid @RequestBody PrestationRequest request) {
        return ResponseEntity.ok(prestationService.createPrestationGenerique(request));
    }

    /**
     * Cree une prestation de type "Circuit" associee a ses etapes et contraintes logistiques.
     * <p>
     * Acces restreint. Reserve a l'utilisateur possedant le role ADMIN.
     * </p>
     * * @param request Le DTO specifique CircuitRequest, verifie par le validateur de contraintes.
     * @return Une ResponseEntity contenant le DTO de la prestation creee (avec donnees du circuit) et un code 200 OK.
     */
    @PostMapping("/circuit")
    public ResponseEntity<PrestationResponse> createCircuit(@Valid @RequestBody CircuitRequest request) {
        return ResponseEntity.ok(prestationService.createCircuit(request));
    }

    /**
     * Cree une prestation de type "Cours de langue" liee a ses specifications pedagogiques.
     * <p>
     * Acces restreint. Reserve a l'utilisateur possedant le role ADMIN.
     * </p>
     * * @param request Le DTO specifique CoursLangueRequest, soumis aux regles de validation du payload.
     * @return Une ResponseEntity contenant le DTO de la prestation creee (avec donnees de cours) et un code 200 OK.
     */
    @PostMapping("/courslangue")
    public ResponseEntity<PrestationResponse> createCoursLangue(@Valid @RequestBody CoursLangueRequest request) {
        return ResponseEntity.ok(prestationService.createCoursLangue(request));
    }

    /**
     * Supprime definitivement une prestation du catalogue a partir de son identifiant.
     * <p>
     * Acces restreint. Reserve a l'utilisateur possedant le role ADMIN.
     * L'operation applique les regles de suppression ou de cascade definies au niveau JPA/SQL.
     * </p>
     * * @param id L'identifiant de la prestation a supprimer.
     * @return Une ResponseEntity vide avec un code 200 OK pour confirmer la destruction.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrestation(@PathVariable Long id) {
        prestationService.deletePrestation(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Ajoute ou met à jour une métadonnée spécifique pour une prestation donnée.
     * 
     * @param id Identifiant de la prestation
     * @param metadata Map contenant les clés et valeurs à mettre à jour
     * @return ResponseEntity contenant un message de confirmation et un code 200 OK
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/{id}/metadata")
    public ResponseEntity<Map<String, Object>> updateMetadata(
            @PathVariable("id") Long id, 
            @RequestBody Map<String, Object> metadata) {
        
        // 1. Validation basique du payload
        if (metadata == null || metadata.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La liste des métadonnées est vide."));
        }

        // 2. Mise à jour via le service
        metadata.forEach((key, value) -> prestationService.addOrUpdateMetadata(id, key, value));
        
        // 3. Construction de la réponse explicite
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Métadonnées mises à jour avec succès pour la prestation " + id);
        response.put("updatedFields", metadata.keySet());
        response.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Récupère uniquement les métadonnées d'une prestation.
     * @param id Identifiant de la prestation
     * @return Map contenant les métadonnées
     */
    @GetMapping("/{id}/metadata")
    public ResponseEntity<Map<String, Object>> getMetadata(@PathVariable Long id) {
        // On récupère la prestation complète via le service existant
        PrestationResponse prestation = prestationService.getPrestationById(id);
        
        // Note : Il faudra s'assurer que ton Mapper inclut bien le champ 'metadata' 
        // dans la classe PrestationResponse.
        return ResponseEntity.ok(prestation.getMetadata());
    }
    
    /**
     * Recherche avancée de prestations par critères géographiques issus des métadonnées.
     * <p>
     * Cette méthode permet de filtrer le catalogue dynamiquement selon le lieu de départ 
     * ou d'arrivée souhaité par le client.
     * </p>
     * * @param depart Optionnel. Filtre les prestations par lieu de départ.
     * @param arrivee Optionnel. Filtre les prestations par lieu d'arrivée.
     * @return Une liste de prestations filtrées.
     */
    @GetMapping("/search")
    public ResponseEntity<List<PrestationResponse>> searchByLocation(
            @RequestParam(required = false) String depart,
            @RequestParam(required = false) String arrivee) {
        
        List<Prestation> resultats;

        if (depart != null) {
            resultats = prestationRepository.findByLieuDepart(depart);
        } else if (arrivee != null) {
            resultats = prestationRepository.findByLieuArrivee(arrivee);
        } else {
            // Si aucun filtre n'est fourni, on renvoie tout le catalogue
            return getAllPrestations();
        }

        return ResponseEntity.ok(resultats.stream()
                .map(prestationMapper::toGenericResponse)
                .collect(Collectors.toList()));
    }
    
    /**
     * Recherche avancée de prestations par filtrage d'itinéraire.
     * <p>
     * Endpoint optimisé permettant au Front-end d'extraire les offres de voyages 
     * correspondant à un trajet complet (Départ -> Arrivée).
     * </p>
     * @param depart Le lieu de départ.
     * @param arrivee Le lieu d'arrivée.
     * @return Une ResponseEntity contenant la liste des résultats et un code 200 OK.
     */
    @GetMapping("/search/trajet")
    public ResponseEntity<List<PrestationResponse>> getByTrajet(
            @RequestParam String depart, 
            @RequestParam String arrivee) {
        
        return ResponseEntity.ok(prestationService.findByTrajet(depart, arrivee));
    }
}