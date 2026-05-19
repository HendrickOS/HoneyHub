package fr.honeygroup.controller;

import fr.honeygroup.bll.PrestationService;
import fr.honeygroup.bo.request.CircuitRequest;
import fr.honeygroup.bo.request.CoursLangueRequest;
import fr.honeygroup.bo.request.PrestationRequest;
import fr.honeygroup.bo.response.PrestationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}