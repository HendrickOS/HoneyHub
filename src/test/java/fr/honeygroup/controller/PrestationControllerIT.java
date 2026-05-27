package fr.honeygroup.controller;

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
@ActiveProfiles("test") // Utilise application-test.properties
class PrestationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DemandeLeadRepository demandeLeadRepository;

    @Autowired
    private PrestationRepository prestationRepository;

    @Autowired
    private PoleRepository poleRepository;

    private Pole pole;

    @BeforeEach
    void setUp() {
        // Préparer la base H2 en nettoyant dans le bon ordre (les enfants d'abord)
        demandeLeadRepository.deleteAll();
        prestationRepository.deleteAll();
        poleRepository.deleteAll();
        
        pole = Pole.builder().nom("Séjours").description("Vacances").build();
        pole = poleRepository.save(pole);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void testGetPrestations_AsClient_Success() throws Exception {
        mockMvc.perform(get("/api/prestations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void testCreateCircuit_AsClient_Forbidden() throws Exception {
        String json = """
                {
                  "poleId": %d,
                  "titreService": "Tour du monde",
                  "description": "Un super tour du monde à la découverte des merveilles sauvages",
                  "prixBase": 5000,
                  "descriptionLongue": "Un descriptif très complet et extrêmement détaillé de notre tour du monde unique en son genre",
                  "itineraire": "Paris -> Tokyo",
                  "duree": "1 mois"
                }
                """.formatted(pole.getId());

        // Le client n'a pas le rôle ADMIN, donc ça doit être 403 Forbidden
        mockMvc.perform(post("/api/prestations/circuit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCircuit_AsAdmin_Success() throws Exception {
        String json = """
                {
                  "poleId": %d,
                  "titreService": "Tour du monde",
                  "description": "Un super tour du monde à la découverte des merveilles sauvages",
                  "prixBase": 5000,
                  "descriptionLongue": "Un descriptif très complet et extrêmement détaillé de notre tour du monde unique en son genre",
                  "itineraire": "Paris -> Tokyo",
                  "duree": "1 mois"
                }
                """.formatted(pole.getId());

        mockMvc.perform(post("/api/prestations/circuit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("CIRCUIT"))
                .andExpect(jsonPath("$.titreService").value("Tour du monde"));
    }
}
