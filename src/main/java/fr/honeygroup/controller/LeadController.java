package fr.honeygroup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import fr.honeygroup.bll.LeadService;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import lombok.RequiredArgsConstructor;

/**
 * Contrôleur REST exposant les points de terminaison (endpoints) pour la gestion des opportunités commerciales (Leads).
 * <p>
 * Ce composant assure la réception des requêtes de prospection ou d'expressions de besoins, 
 * puis délègue le traitement transactionnel à la couche métier (BLL) avant de restituer la ressource 
 * encapsulée dans une réponse HTTP normalisée sous la racine {@code /api/leads}.
 * </p>
 */
@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    /**
     * Intercepte la soumission d'un formulaire de contact ou d'expression de besoin pour générer un nouveau Lead.
     * <p>
     * La payload JSON reçue est désérialisée en DTO de requête, validée et convertie au sein de la couche métier, 
     * puis retournée au client enveloppée dans une entité de réponse HTTP formalisant le succès de l'opération.
     * </p>
     * @param request Objet DTO contenant les métadonnées de l'opportunité d'affaires et ses détails spécifiques (EAV).
     * @return Un {@link ResponseEntity} configuré sur le statut HTTP 200 OK et transportant le corps du {@link LeadResponse}.
     */
    @PostMapping
    public ResponseEntity<LeadResponse> createLead(@RequestBody LeadRequest request) {
        return ResponseEntity.ok(leadService.createLead(request));
    }
    
    @GetMapping
    public ResponseEntity<List<LeadResponse>> getAllLeads() {
        return ResponseEntity.ok(leadService.getAllLeads());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponse> getLeadById(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LeadResponse> updateLeadStatus(@PathVariable Long id, @RequestParam enumeration.StatutLead statut) {
        return ResponseEntity.ok(leadService.updateLeadStatus(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok().build();
    }
}