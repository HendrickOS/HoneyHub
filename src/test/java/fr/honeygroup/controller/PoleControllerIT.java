package fr.honeygroup.controller;

import fr.honeygroup.bo.Pole;
import fr.honeygroup.repository.DemandeLeadRepository;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.repository.PrestationRepository;

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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PoleControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private PoleRepository poleRepository;
    @Autowired private DemandeLeadRepository demandeLeadRepository;
    @Autowired private PrestationRepository prestationRepository;

    private Pole pole;

    @BeforeEach
    void setUp() {

        demandeLeadRepository.deleteAll();
        prestationRepository.deleteAll();
        poleRepository.deleteAll();

        pole = Pole.builder()
                .nom("Tourisme")
                .description("Pôle d'écotourisme solidaire")
                .build();

        pole = poleRepository.save(pole);
    }

    // =========================================================
    // GET ALL POLES
    // =========================================================

    @Test
    void getAllPoles_success() throws Exception {

        mockMvc.perform(get("/api/poles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Tourisme"))
                .andExpect(jsonPath("$[0].description").value("Pôle d'écotourisme solidaire"));
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getPoleById_success() throws Exception {

        mockMvc.perform(get("/api/poles/" + pole.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pole.getId()))
                .andExpect(jsonPath("$.nom").value("Tourisme"));
    }

    @Test
    void getPoleById_notFound() throws Exception {

        mockMvc.perform(get("/api/poles/9999"))
                .andExpect(status().isInternalServerError());
    }

    // =========================================================
    // SEARCH BY NAME
    // =========================================================

    @Test
    void getPoleByName_success() throws Exception {

        mockMvc.perform(get("/api/poles/search")
                        .param("nom", "Tourisme"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Tourisme"));
    }

    // =========================================================
    // CREATE POLE (SECURED)
    // =========================================================

    
    
   
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createPole_success_forAdmin() throws Exception {

        String json = """
        {
          "nom": "Nouveau Pole",
          "description": "Description longue de test"
        }
        """;

        mockMvc.perform(post("/api/poles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "CLIENT")
    void createPole_forbidden_forClient() throws Exception {

        String json = """
        {
          "nom": "Nouveau Pole",
          "description": "Description longue de test"
        }
        """;

        mockMvc.perform(post("/api/poles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

   

    // =========================================================
    // DELETE POLE (SECURED)
    // =========================================================

    @Test
    @WithMockUser(roles = "CLIENT")
    void deletePole_forbidden_forClient() throws Exception {

        mockMvc.perform(delete("/api/poles/" + pole.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePole_success_forAdmin() throws Exception {

        mockMvc.perform(delete("/api/poles/" + pole.getId()))
                .andExpect(status().isOk());
    }
}