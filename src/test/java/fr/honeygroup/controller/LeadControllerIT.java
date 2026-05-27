package fr.honeygroup.controller;

import fr.honeygroup.bo.*;
import fr.honeygroup.enumeration.Role;
import fr.honeygroup.enumeration.StatutLead;
import fr.honeygroup.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LeadControllerIT {

    @Autowired private MockMvc mockMvc;

    @Autowired private DemandeLeadRepository demandeLeadRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PoleRepository poleRepository;
    @Autowired private PrestationRepository prestationRepository;

    private DemandeLead lead;
    private Pole pole;
    private User user;

    @BeforeEach
    void setUp() {

        demandeLeadRepository.deleteAll();
        prestationRepository.deleteAll();
        poleRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setNom("Doe");
        user.setPrenom("John");
        user.setEmail("test@honey.com");
        user.setPassword("password123!");
        user.setRole(Role.CLIENT);
        user = userRepository.save(user);

        pole = Pole.builder()
                .nom("Pole Test")
                .build();
        pole = poleRepository.save(pole);

        lead = DemandeLead.builder()
                .user(user)
                .pole(pole)
                .source("Web")
                .statut(StatutLead.NOUVEAU)
                .build();

        lead = demandeLeadRepository.save(lead);
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    @WithMockUser(roles = "CLIENT")
    void getAllLeads_forbidden_forClient() throws Exception {

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getAllLeads_success_forManager() throws Exception {

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statut").value("NOUVEAU"));
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    @WithMockUser(roles = "MANAGER")
    void getLeadById_success() throws Exception {

        mockMvc.perform(get("/api/leads/" + lead.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lead.getId()));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void getLeadById_forbidden_forClient() throws Exception {

        mockMvc.perform(get("/api/leads/" + lead.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getLeadById_notFound() throws Exception {

        mockMvc.perform(get("/api/leads/99999"))
                .andExpect(status().isInternalServerError());
    }

    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateLeadStatus_success() throws Exception {

        mockMvc.perform(put("/api/leads/" + lead.getId() + "/status")
                        .param("statut", "EN_COURS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EN_COURS"));
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteLead_success_forAdmin() throws Exception {

        mockMvc.perform(delete("/api/leads/" + lead.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void deleteLead_forbidden_forClient() throws Exception {

        mockMvc.perform(delete("/api/leads/" + lead.getId()))
                .andExpect(status().isForbidden());
    }

    // =========================================================
    // CREATE LEAD (PUBLIC)
    // =========================================================

    @Test
    void createLead_success() throws Exception {

        String json = """
        {
          "poleId": %d,
          "nom": "Alice Visitor",
          "email": "alice@visitor.com",
          "source": "Web",
          "specificDetails": {
            "key": "val"
          }
        }
        """.formatted(pole.getId());

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createLead_invalidEmail_error() throws Exception {

        String json = """
        {
          "poleId": %d,
          "nom": "Alice Visitor",
          "email": "invalid-email",
          "source": "Web",
          "specificDetails": {
            "key": "val"
          }
        }
        """.formatted(pole.getId());

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError());
    }
}