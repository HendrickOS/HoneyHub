package fr.honeygroup.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import fr.honeygroup.bo.Session;
import fr.honeygroup.enumeration.StatutSession;
import fr.honeygroup.repository.SessionRepository;

@SpringBootTest // Charge tout le contexte Spring (plus lourd, mais test réel)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Utilise application-test.properties (avec H2)
@Transactional // Rollback automatique après chaque test pour ne pas polluer la DB
class SessionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Test d'intégration : La transition illégale de CLOTURE vers OUVERT doit échouer")
    void shouldRefuseIllegalTransitionInIntegration() throws Exception {
        // 1. Préparation d'une session valide dans la base H2
        Session session = Session.builder()
                .dateDebut(LocalDateTime.now().plusDays(1)) // Date future
                .dateFin(LocalDateTime.now().plusDays(5))
                .capaciteMax(10)
                .statutSession(StatutSession.CLOTURE) // On met l'état que l'on veut tester
                .build();
        session = sessionRepository.save(session);

        // 2. Tentative de transition vers OUVERT (illégal selon l'Enum)
        mockMvc.perform(post("/api/sessions/{id}/transition", session.getId())
                .param("nouveauStatut", "OUVERT")
                .with(csrf()))
                .andExpect(status().isForbidden()); // On attend une erreur 403
    }
}