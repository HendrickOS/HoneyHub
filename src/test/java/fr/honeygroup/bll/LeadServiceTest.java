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
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import fr.honeygroup.mapper.LeadMapper;
import fr.honeygroup.repository.DemandeLeadRepository;
import fr.honeygroup.repository.DetailsSpecifiquesRepository;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.repository.UserRepository;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service LeadService")
class LeadServiceTest {

    @Mock
    private DemandeLeadRepository leadRepository;

    @Mock
    private PoleRepository poleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LeadMapper leadMapper;

    @InjectMocks
    private LeadServiceImpl leadService;

    @Test
    @DisplayName("Lead : Création avec détails spécifiques (EAV)")
    void createLead_ShouldSaveLeadAndDetails() {
        // 1. Préparation
        LeadRequest request = new LeadRequest();
        request.setPoleId(1L);
        request.setEmail("contact@honeygroup.fr");
        request.setNom("Nom de contact");
        Map<String, String> details = new HashMap<>();
        details.put("technologie_cible", "Java");
        request.setSpecificDetails(details);

        Pole pole = new Pole();
        pole.setId(1L);

        when(poleRepository.findById(1L)).thenReturn(Optional.of(pole));
        when(leadRepository.save(any(DemandeLead.class))).thenAnswer(i -> {
            DemandeLead lead = (DemandeLead) i.getArguments()[0];
            lead.setId(100L);
            return lead;
        });
        when(leadMapper.toResponse(any(DemandeLead.class))).thenAnswer(i -> {
            DemandeLead lead = (DemandeLead) i.getArguments()[0];
            return LeadResponse.builder().id(lead.getId()).build();
        });

        // 2. Exécution
        LeadResponse response = leadService.createLead(request);

        // 3. Vérifications
        assertThat(response.getId()).isEqualTo(100L);
        verify(leadRepository, times(1)).save(any(DemandeLead.class));
    }
}