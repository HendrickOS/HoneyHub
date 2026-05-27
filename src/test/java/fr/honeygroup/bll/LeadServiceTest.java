package fr.honeygroup.bll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.honeygroup.bll.impl.LeadServiceImpl;
import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import fr.honeygroup.repository.DemandeLeadRepository;
import fr.honeygroup.repository.DetailsSpecifiquesRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service LeadService")
class LeadServiceTest {

    @Mock
    private DemandeLeadRepository leadRepository;

    @Mock
    private DetailsSpecifiquesRepository detailsRepository;

    @InjectMocks
    private LeadServiceImpl leadService;

    @Test
    @DisplayName("Lead : Création avec détails spécifiques (EAV)")
    void createLead_ShouldSaveLeadAndDetails() {
        // 1. Préparation
        LeadRequest request = new LeadRequest();
        request.setPoleId(1L);
        Map<String, String> details = new HashMap<>();
        details.put("technologie_cible", "Java");
        request.setSpecificDetails(details);

        when(leadRepository.save(any(DemandeLead.class))).thenAnswer(i -> {
            DemandeLead lead = (DemandeLead) i.getArguments()[0];
            lead.setId(100L);
            return lead;
        });

        // 2. Exécution
        LeadResponse response = leadService.createLead(request);

        // 3. Vérifications
        assertThat(response.getId()).isEqualTo(100L);
        verify(leadRepository, times(1)).save(any(DemandeLead.class));
        // Vérifie que les détails sont également persistés
        verify(detailsRepository, atLeastOnce()).save(any());
    }
}