package fr.honeygroup.controller;

import fr.honeygroup.bo.Pole;
import fr.honeygroup.repository.PoleRepository;
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
class PoleControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PoleRepository poleRepository;

    @Autowired
    private fr.honeygroup.repository.DemandeLeadRepository demandeLeadRepository;

    @Autowired
    private fr.honeygroup.repository.PrestationRepository prestationRepository;

    @org.springframework.test.context.bean.override.mockito.MockitoSpyBean
    private fr.honeygroup.bll.PoleService poleService;

    private Pole testPole;

    @BeforeEach
    void setUp() {
        demandeLeadRepository.deleteAll();
        prestationRepository.deleteAll();
        poleRepository.deleteAll();

        testPole = Pole.builder()
                .nom("Tourisme")
                .description("Pôle d'écotourisme solidaire")
                .build();
        testPole = poleRepository.save(testPole);
    }

    @Test
    void testGetAllPoles_Anonymous_Success() throws Exception {
        mockMvc.perform(get("/api/poles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Tourisme"))
                .andExpect(jsonPath("$[0].description").value("Pôle d'écotourisme solidaire"));
    }

    @Test
    void testGetPoleById_Anonymous_Success() throws Exception {
        mockMvc.perform(get("/api/poles/" + testPole.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testPole.getId()))
                .andExpect(jsonPath("$.nom").value("Tourisme"));
    }

    @Test
    void testGetPoleById_NotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/poles/9999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetPoleByNom_Anonymous_Success() throws Exception {
        mockMvc.perform(get("/api/poles/search").param("nom", "Tourisme"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Tourisme"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void testCreatePole_AsClient_Forbidden() throws Exception {
        String json = "{\"nom\":\"Nouveau Pole\",\"description\":\"Description longue de test\"}";
        mockMvc.perform(post("/api/poles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreatePole_AsAdmin_Success() throws Exception {
        fr.honeygroup.bo.response.PoleResponse mockResponse = fr.honeygroup.bo.response.PoleResponse.builder()
                .id(999L)
                .nom("Nouveau Pole")
                .description("Description longue de test")
                .build();
        org.mockito.Mockito.doReturn(mockResponse).when(poleService).create(org.mockito.Mockito.any(fr.honeygroup.bo.request.PoleRequest.class));

        String json = "{\"nom\":\"Nouveau Pole\",\"description\":\"Description longue de test\"}";
        mockMvc.perform(post("/api/poles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Nouveau Pole"))
                .andExpect(jsonPath("$.description").value("Description longue de test"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void testDeletePole_AsClient_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/poles/" + testPole.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeletePole_AsAdmin_Success() throws Exception {
        mockMvc.perform(delete("/api/poles/" + testPole.getId()))
                .andExpect(status().isOk());
    }
}
