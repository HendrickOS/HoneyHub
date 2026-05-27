package fr.honeygroup.controller;

import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.User;
import fr.honeygroup.repository.DemandeLeadRepository;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.repository.PrestationRepository;
import fr.honeygroup.repository.UserRepository;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DemandeLeadRepository demandeLeadRepository;
    @Autowired
    private PrestationRepository prestationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PoleRepository poleRepository;

    private DemandeLead testLead;

    @BeforeEach
    void setUp() {
        demandeLeadRepository.deleteAll();
        prestationRepository.deleteAll();
        poleRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setNom("Doe");
        user.setPrenom("John");
        user.setEmail("test@honey.com");
        user.setPassword("password123!");
        user.setRole(fr.honeygroup.enumeration.Role.CLIENT);
        user = userRepository.save(user);

        Pole pole = Pole.builder().nom("Pole Test").build();
        pole = poleRepository.save(pole);

        testLead = DemandeLead.builder()
                .user(user)
                .pole(pole)
                .source("Web")
                .statut(fr.honeygroup.enumeration.StatutLead.NOUVEAU)
                .build();
        testLead = demandeLeadRepository.save(testLead);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void testGetAllLeads_AsClient_Forbidden() throws Exception {
        // Seuls Manager et Admin peuvent lister les leads (cf. SecurityConfig)
        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetAllLeads_AsManager_Success() throws Exception {
        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statut").value("NOUVEAU"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testUpdateLeadStatus_AsManager_Success() throws Exception {
        mockMvc.perform(put("/api/leads/" + testLead.getId() + "/status")
                        .param("statut", "EN_COURS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EN_COURS"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetLeadById_AsManager_Success() throws Exception {
        mockMvc.perform(get("/api/leads/" + testLead.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testLead.getId()));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void testGetLeadById_AsClient_Forbidden() throws Exception {
        mockMvc.perform(get("/api/leads/" + testLead.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testGetLeadById_NotFound() throws Exception {
        mockMvc.perform(get("/api/leads/9999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteLead_AsAdmin_Success() throws Exception {
        mockMvc.perform(delete("/api/leads/" + testLead.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void testDeleteLead_AsClient_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/leads/" + testLead.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateLead_AsVisitor_Success() throws Exception {
        String json = "{\n" +
                "  \"poleId\": " + testLead.getPole().getId() + ",\n" +
                "  \"nom\": \"Alice Visitor\",\n" +
                "  \"email\": \"alice@visitor.com\",\n" +
                "  \"source\": \"Web\",\n" +
                "  \"specificDetails\": {\"key\": \"val\"}\n" +
                "}";

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreateLead_ValidationError_WhenEmailInvalid() throws Exception {
        String json = "{\n" +
                "  \"poleId\": " + testLead.getPole().getId() + ",\n" +
                "  \"nom\": \"Alice Visitor\",\n" +
                "  \"email\": \"invalid-email-format\",\n" +
                "  \"source\": \"Web\",\n" +
                "  \"specificDetails\": {\"key\": \"val\"}\n" +
                "}";

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError());
    }
}
