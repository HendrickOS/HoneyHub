package fr.honeygroup.bll.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final DemandeLeadRepository demandeLeadRepository;
    private final PoleRepository poleRepository;
    private final UserRepository userRepository;
    private final LeadMapper leadMapper;

    @Override
    @Transactional

    public LeadResponse createLead(LeadRequest request) {
     
        // 🔥 VALIDATION BUSINESS

        validateBusinessRules(request);
     
        // 🔥 USER (nullable)

        User user = null;
     
        if (request.getUserId() != null) {
     
            user = userRepository.findById(request.getUserId())

                    .orElseThrow(() -> new RuntimeException("User introuvable"));

        }
     
        // 🔥 POLE obligatoire

        Pole pole = poleRepository.findById(request.getPoleId())

                .orElseThrow(() -> new RuntimeException("Pôle introuvable"));
     
        // 🔥 CREATE LEAD

        DemandeLead lead = DemandeLead.builder()

                .user(user)

                .pole(pole)

                .source(request.getSource())

                .nomContact(request.getNom())

                .emailContact(request.getEmail())

               // .commentaireInterne(request.getCommentaireInterne())

                .build();
     
        // 🔥 DETAILS MAP -> ENTITY

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
     
        // 🔥 SAVE

        DemandeLead savedLead = demandeLeadRepository.save(lead);
     
        return leadMapper.toResponse(savedLead);

    }
     
    private void validateBusinessRules(LeadRequest request) {

        // =========================
        // VISITEUR OBLIGATOIRE
        // =========================
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

        // =========================
        // DETAILS OBLIGATOIRES
        // =========================
        if (request.getSpecificDetails() == null || request.getSpecificDetails().isEmpty()) {
            throw new RuntimeException("Les détails sont obligatoires");
        }

        // =========================
        // ANTI-SPAM
        // =========================
        if (request.getSpecificDetails().size() > 20) {
            throw new RuntimeException("Trop de champs envoyés");
        }
    }
    
     
    

    @Override
    public List<LeadResponse> getAllLeads() {
        return demandeLeadRepository.findAll()
                .stream()
                .map(leadMapper::toResponse)
                .toList();
    }

    @Override
    public LeadResponse getLeadById(Long id) {
        return demandeLeadRepository.findById(id)
                .map(leadMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Lead introuvable"));
    }

    @Override
    @Transactional
    public LeadResponse updateLeadStatus(Long id, enumeration.StatutLead statut) {

        DemandeLead lead = demandeLeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead introuvable"));

        lead.setStatut(statut);

        return leadMapper.toResponse(
                demandeLeadRepository.save(lead)
        );
    }

    @Override
    @Transactional
    public void deleteLead(Long id) {

        if (!demandeLeadRepository.existsById(id)) {
            throw new RuntimeException("Lead introuvable");
        }

        demandeLeadRepository.deleteById(id);
    }
    
    

     
}