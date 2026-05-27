package fr.honeygroup.bll.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.honeygroup.bo.*;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import fr.honeygroup.enumeration.StatutLead;
import fr.honeygroup.mapper.LeadMapper;
import fr.honeygroup.repository.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeadServiceImpl - Tests métier")
class LeadServiceImplTest {

    @Mock private DemandeLeadRepository demandeLeadRepository;
    @Mock private UserRepository userRepository;
    @Mock private PoleRepository poleRepository;
    @Mock private LeadMapper leadMapper;

    @InjectMocks
    private LeadServiceImpl leadService;

    private User user;
    private Pole pole;
    private LeadRequest request;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("client@honey.com");

        pole = new Pole();
        pole.setId(1L);
        pole.setNom("Voyage");

        request = new LeadRequest();
        request.setPoleId(1L);
        request.setNom("Jean Dupont");
        request.setEmail("jean.dupont@test.fr");
        request.setSource("Web");

        Map<String, String> details = new HashMap<>();
        details.put("budget", "1500");
        request.setSpecificDetails(details);
    }

    // =========================================================
    // CREATE LEAD - BUSINESS RULES
    // =========================================================

    @Test
    @DisplayName("Création lead - succès visiteur anonyme")
    void createLead_Anonymous_Success() {

        when(poleRepository.findById(1L)).thenReturn(Optional.of(pole));
        when(demandeLeadRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(leadMapper.toResponse(any())).thenReturn(new LeadResponse());

        LeadResponse result = leadService.createLead(request);

        assertNotNull(result);
        verify(userRepository, never()).findById(any());
        verify(demandeLeadRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Création lead - succès utilisateur connecté")
    void createLead_UserConnected_Success() {

        request.setUserId(1L);
        request.setNom(null);
        request.setEmail(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(poleRepository.findById(1L)).thenReturn(Optional.of(pole));
        when(demandeLeadRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(leadMapper.toResponse(any())).thenReturn(new LeadResponse());

        LeadResponse result = leadService.createLead(request);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Création lead - pôle introuvable")
    void createLead_PoleNotFound() {

        when(poleRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> leadService.createLead(request)
        );

        assertEquals("Pôle introuvable", ex.getMessage());
    }

    @Test
    @DisplayName("Création lead - email invalide")
    void createLead_InvalidEmail() {

        request.setEmail("invalid-email");

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> leadService.createLead(request)
        );

        assertEquals("Email invalide", ex.getMessage());
    }

    @Test
    @DisplayName("Création lead - détails obligatoires")
    void createLead_MissingDetails() {

        request.setSpecificDetails(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> leadService.createLead(request)
        );

        assertEquals("Les détails sont obligatoires", ex.getMessage());

        verify(demandeLeadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Création lead - anti spam")
    void createLead_AntiSpam() {

        Map<String, String> spam = new HashMap<>();
        for (int i = 0; i < 21; i++) {
            spam.put("k" + i, "v" + i);
        }

        request.setSpecificDetails(spam);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> leadService.createLead(request)
        );

        assertEquals("Trop de champs envoyés", ex.getMessage());
    }

    // =========================================================
    // GET
    // =========================================================

    @Test
    void getAllLeads() {

        when(demandeLeadRepository.findAll())
                .thenReturn(List.of(new DemandeLead(), new DemandeLead()));

        when(leadMapper.toResponse(any())).thenReturn(new LeadResponse());

        List<LeadResponse> result = leadService.getAllLeads();

        assertEquals(2, result.size());
    }

    @Test
    void getLeadById_success() {

        DemandeLead lead = new DemandeLead();
        lead.setId(1L);

        when(demandeLeadRepository.findById(1L))
                .thenReturn(Optional.of(lead));

        when(leadMapper.toResponse(lead))
                .thenReturn(LeadResponse.builder().id(1L).build());

        LeadResponse result = leadService.getLeadById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getLeadById_notFound() {

        when(demandeLeadRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> leadService.getLeadById(99L)
        );

        assertEquals("Lead introuvable", ex.getMessage());
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void updateStatus_success() {

        DemandeLead lead = new DemandeLead();
        lead.setStatut(StatutLead.NOUVEAU);

        when(demandeLeadRepository.findById(1L))
                .thenReturn(Optional.of(lead));

        when(demandeLeadRepository.save(any())).thenReturn(lead);

        leadService.updateLeadStatus(1L, StatutLead.TRAITE);

        assertEquals(StatutLead.TRAITE, lead.getStatut());

        verify(demandeLeadRepository, times(1)).save(lead);
    }

    @Test
    void updateStatus_notFound() {

        when(demandeLeadRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> leadService.updateLeadStatus(99L, StatutLead.TRAITE)
        );

        assertEquals("Lead introuvable", ex.getMessage());
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void delete_success() {

        when(demandeLeadRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> leadService.deleteLead(1L));

        verify(demandeLeadRepository).deleteById(1L);
    }

    @Test
    void delete_notFound() {

        when(demandeLeadRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> leadService.deleteLead(99L)
        );

        assertEquals("Lead introuvable", ex.getMessage());

        verify(demandeLeadRepository, never()).deleteById(any());
    }
}