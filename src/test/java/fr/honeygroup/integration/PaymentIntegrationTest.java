package fr.honeygroup.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback automatique après chaque test pour ne pas polluer la BDD
public class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN") // Simule un utilisateur ADMIN
    void validerPaiement_ShouldReturn200_WhenValidAdminRequest() throws Exception {
        // En supposant que le paiement ID 1 existe en base de test
        mockMvc.perform(post("/api/payments/1/valider")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER") // Simule un utilisateur standard (sans les droits)
    void validerPaiement_ShouldReturn403_WhenUserIsNotStaff() throws Exception {
        mockMvc.perform(post("/api/payments/1/valider")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}