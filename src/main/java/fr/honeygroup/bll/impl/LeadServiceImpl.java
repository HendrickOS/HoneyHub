package fr.honeygroup.bll.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.honeygroup.bll.LeadService;
import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.DetailsSpecifiques;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import fr.honeygroup.mapper.LeadMapper;
import fr.honeygroup.repository.DemandeLeadRepository;
import fr.honeygroup.repository.PrestationRepository;
import fr.honeygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final DemandeLeadRepository demandeLeadRepository;
    private final UserRepository userRepository;
    private final PrestationRepository prestationRepository;
    private final LeadMapper leadMapper;

    @Transactional
    public LeadResponse createLead(LeadRequest request) {

        validateDetails(request.getSpecificDetails());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User introuvable"));
//a voir
        Prestation prestation = prestationRepository.findById(request.getPrestationId())
                .orElseThrow(() -> new RuntimeException("Prestation introuvable"));

        // 🟢 création lead
        DemandeLead lead = DemandeLead.builder()
                .user(user)
                .prestation(prestation)
                .source(request.getSource())
                .build();

        // 🟢 mapping details -> entity
        List<DetailsSpecifiques> detailsList = request.getSpecificDetails()
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

        DemandeLead saveDemandeLead = demandeLeadRepository.save(lead);

        return leadMapper.toResponse(saveDemandeLead);
    }

    // 🔥 validation simple
    private void validateDetails(Map<String, String> details) {
        if (details == null || details.isEmpty()) {
            throw new RuntimeException("Details obligatoires");
        }
    }
}