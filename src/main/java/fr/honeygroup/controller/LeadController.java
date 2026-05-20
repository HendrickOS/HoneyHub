package fr.honeygroup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import fr.honeygroup.bll.LeadService;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import lombok.RequiredArgsConstructor;

/**
 * Controleur REST exposant les points de terminaison (endpoints) pour la gestion des opportunites commerciales (Leads).
 * <p>
 * Ce composant assure la reception des requetes de prospection ou d'expressions de besoins, 
 * puis delegue le traitement transactionnel a la couche metier (BLL) avant de restituer la ressource 
 * encapsulee dans une reponse HTTP normalisee sous la racine {@code /api/leads}.
 * </p>
 */
@RestController
@RequestMapping("/api/leads")
@CrossOrigin
@RequiredArgsConstructor
public class LeadController {

    /** Service metier pilotant le cycle de vie des leads et de leurs attributs dynamiques. */
    private final LeadService leadService;

    /**
     * Intercepte la soumission d'un formulaire de contact ou d'expression de besoin pour generer un nouveau Lead.
     * <p>
     * La payload JSON recue est deserialisee en DTO de requete, validee et convertie au sein de la couche metier, 
     * puis retournee au client enveloppee dans une entite de reponse HTTP formalisant le succes de l'operation.
     * </p>
     * * @param request Objet DTO contenant les metadonnees de l'opportunite d'affaires et ses details specifiques (EAV).
     * @return Un {@link ResponseEntity} configure sur le statut HTTP 200 OK et transportant le corps du {@link LeadResponse}.
     */
    @PostMapping
    public ResponseEntity<LeadResponse> createLead(@RequestBody LeadRequest request) {
        return ResponseEntity.ok(leadService.createLead(request));
    }
    
    /**
     * Recupere la liste exhaustive de tous les leads enregistres dans le systeme.
     * <p>
     * Acces restreint. Cet endpoint fournit une vue d'ensemble operationnelle reservee 
     * aux membres de l'equipe commerciale et technique (Rôles MANAGER et ADMIN).
     * </p>
     * * @return Une ResponseEntity contenant la liste des DTOs LeadResponse et un code 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<LeadResponse>> getAllLeads() {
        return ResponseEntity.ok(leadService.getAllLeads());
    }

    /**
     * Extrait les informations completes d'un lead specifique a partir de son identifiant unique.
     * <p>
     * Acces restreint. Permet au Staff (MANAGER/ADMIN) d'analyser en profondeur les besoins metiers 
     * exprimes par un prospect (y compris les couples de donnees dynamiques EAV).
     * </p>
     * * @param id L'identifiant unique du lead recherche en base de donnees.
     * @return Une ResponseEntity enveloppant le DTO LeadResponse trouve et un code 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LeadResponse> getLeadById(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    /**
     * Met a jour le statut transitionnel d'un lead au cours de son cycle de traitement.
     * <p>
     * Acces restreint. Permet aux roles MANAGER et ADMIN de modifier l'etat d'avancement du lead 
     * (ex: traitement en cours, transforme en client, refuse) via un parametre nomme.
     * </p>
     * * @param id L'identifiant unique du lead a modifier.
     * @param statut La nouvelle valeur de l'enumeration StatutLead a appliquer.
     * @return Une ResponseEntity contenant le DTO LeadResponse mis a jour et un code 200 OK.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<LeadResponse> updateLeadStatus(@PathVariable Long id, @RequestParam enumeration.StatutLead statut) {
        return ResponseEntity.ok(leadService.updateLeadStatus(id, statut));
    }

    /**
     * Supprime definitivement un lead du systeme d'information.
     * <p>
     * Acces restreint critique. Cette operation destructive est strictement reservee 
     * a l'utilisateur possedant le role ADMIN pour garantir l'integrite de l'historique commercial.
     * </p>
     * * @param id L'identifiant du lead a purger.
     * @return Une ResponseEntity vide accompagnee du statut HTTP 200 OK pour valider la suppression.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok().build();
    }
}