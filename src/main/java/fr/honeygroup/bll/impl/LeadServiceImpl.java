package fr.honeygroup.bll.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.honeygroup.bll.LeadService;
import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.DetailsSpecifiques;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import fr.honeygroup.mapper.LeadMapper;
import fr.honeygroup.repository.DemandeLeadRepository;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Implémentation du service de gestion des opportunités commerciales (Leads).
 * Centralise les règles métiers liées au recrutement de leads anonymes (visiteurs) 
 * ou authentifiés (clients) pour l'ensemble des pôles d'activité d'Honey Group.
 *
 */
@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final DemandeLeadRepository demandeLeadRepository;
    private final PoleRepository poleRepository;
    private final UserRepository userRepository;
    private final LeadMapper leadMapper;

    /**
     * Enregistre une nouvelle opportunité commerciale (Lead) dans le système.
     * Cette méthode gère de manière polymorphe les profils connectés et anonymes,
     * puis convertit dynamiquement la Map d'attributs spécifiques au pôle en entités persistantes.
     *
     * @param request Le DTO contenant les informations de contact et les métadonnées spécifiques du lead.
     * @return Un {@link LeadResponse} standardisé contenant le statut initialisé et les identifiants générés.
     * @throws RuntimeException Si les validations métiers échouent ou si les entités liées sont introuvables.
     */
    @Override
    @Transactional
    public LeadResponse createLead(LeadRequest request) {
         
        // 1. Validation de surface et application des invariants métiers
        validateBusinessRules(request);
         
        // 2. Traitement contextuel de l'utilisateur (Optionnel / Nullable)
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User introuvable"));
        }
         
        // 3. Récupération du pôle d'activité associé (Obligatoire)
        Pole pole = poleRepository.findById(request.getPoleId())
                .orElseThrow(() -> new RuntimeException("Pôle introuvable"));
         
        // 4. Instanciation de la structure maîtresse du Lead via le pattern Builder
        DemandeLead lead = DemandeLead.builder()
                .user(user)
                .pole(pole)
                .source(request.getSource())
                .nomContact(request.getNom())
                .emailContact(request.getEmail())
                // .commentaireInterne(request.getCommentaireInterne()) // Réservé pour évolutions futures du CRM
                .build();
         
        // 5. Pivotement fonctionnel : Transformation de la Map de détails en liste d'entités associées
        List<DetailsSpecifiques> detailsList = Optional.ofNullable(request.getSpecificDetails())
                .orElse(new HashMap<>())
                .entrySet()
                .stream()
                .map(entry -> DetailsSpecifiques.builder()
                        .champCle(entry.getKey())
                        .valeur(entry.getValue())
                        .demandeLead(lead)
                        .build()
                )
                .toList();
         
        lead.setSpecificDetails(detailsList);
         
        // 6. Persistance globale de l'arbre d'entités (sauvegarde en cascade)
        DemandeLead savedLead = demandeLeadRepository.save(lead);
         
        return leadMapper.toResponse(savedLead);
    }
     
    /**
     * Valide l'intégrité des données d'entrée par rapport aux contraintes fonctionnelles imposées par le CRM.
     * Les règles appliquées incluent :
     * <ul>
     * <li>L'obligation de fournir un nom et un email syntaxiquement valide pour tout visiteur non connecté.</li>
     * <li>La présence obligatoire d'au moins un attribut dynamique spécifique au besoin du pôle.</li>
     * <li>Un mécanisme de protection anti-spam limitant le nombre de critères par requête.</li>
     * </ul>
     *
     * @param request Le DTO de création de lead à auditer.
     * @throws RuntimeException Si une règle métier ou un format de donnée est enfreint.
     */
    private void validateBusinessRules(LeadRequest request) {

        // --- RÈGLE METIER 1 : Identification obligatoire pour les comptes Visiteurs ---
        if (request.getUserId() == null) {
            if (request.getNom() == null || request.getNom().isBlank()) {
                throw new RuntimeException("Le nom est obligatoire pour un visiteur");
            }

            if (request.getEmail() == null || request.getEmail().isBlank()) {
                throw new RuntimeException("L'email est obligatoire pour un visiteur");
            }

            if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new RuntimeException("Email invalide");
            }
        }

        // --- RÈGLE METIER 2 : Qualification minimale obligatoire ---
        if (request.getSpecificDetails() == null || request.getSpecificDetails().isEmpty()) {
            throw new RuntimeException("Les détails sont obligatoires");
        }

        // --- RÈGLE METIER 3 : Protection Infrastructure & Anti-Spam (Limitation de charge payload) ---
        if (request.getSpecificDetails().size() > 20) {
            throw new RuntimeException("Trop de champs envoyés");
        }
    }
    
    /**
     * Récupère l'intégralité des leads enregistrés sur la plateforme Honey Group.
     * Destiné aux dashboards de supervision globale pour les rôles d'administration.
     *
     * @return Une liste de {@link LeadResponse} (potentiellement vide).
     */
    @Override
    public List<LeadResponse> getAllLeads() {
        return demandeLeadRepository.findAll()
                .stream()
                .map(leadMapper::toResponse)
                .toList();
    }

    /**
     * Recherche un lead spécifique par son identifiant unique.
     *
     * @param id L'identifiant de la demande de lead recherchée.
     * @return Le DTO descriptif complet du lead.
     * @throws RuntimeException Si aucun lead ne correspond à l'ID fourni.
     */
    @Override
    public LeadResponse getLeadById(Long id) {
        return demandeLeadRepository.findById(id)
                .map(leadMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Lead introuvable"));
    }

    /**
     * Modifie le statut d'avancement d'un lead (ex: Traité, En attente, Archivé).
     * Opération hautement critique soumise au workflow de traitement du personnel Staff.
     *
     * @param id L'identifiant unique du dossier.
     * @param statut Le nouveau statut d'énumération à appliquer.
     * @return Le DTO mis à jour reflétant le changement d'état.
     * @throws RuntimeException Si la ressource ciblée n'existe pas en base de données.
     */
    @Override
    @Transactional
    public LeadResponse updateLeadStatus(Long id, fr.honeygroup.enumeration.StatutLead statut) {

        DemandeLead lead = demandeLeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead introuvable"));

        lead.setStatut(statut);

        return leadMapper.toResponse(
                demandeLeadRepository.save(lead)
        );
    }

    /**
     * Supprime définitivement une opportunité commerciale du référentiel.
     *
     * @param id L'identifiant unique de la ressource à purger.
     * @throws RuntimeException Si la cible n'existe pas, empêchant l'annulation de la transaction.
     */
    @Override
    @Transactional
    public void deleteLead(Long id) {

        if (!demandeLeadRepository.existsById(id)) {
            throw new RuntimeException("Lead introuvable");
        }

        demandeLeadRepository.deleteById(id);
    }
}