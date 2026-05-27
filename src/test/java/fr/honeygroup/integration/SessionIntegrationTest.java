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

import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.Session;
import fr.honeygroup.enumeration.StatutSession;
import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SessionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Test d'intégration : La transition illégale de CLOTURE vers OUVERT doit échouer")
    void shouldRefuseIllegalTransitionInIntegration() throws Exception {
    	// 1. Préparation : Créer et persister un Pole (dépendance de la prestation)
        fr.honeygroup.bo.Pole pole = new fr.honeygroup.bo.Pole();
        pole.setNom("Pôle Écotourisme"); // Assure-toi d'avoir un setter pour le nom ou autre champ requis
        entityManager.persist(pole);
        
        // 2. Préparation : Créer et persister une Prestation avec les champs requis
        Prestation prestation = new Prestation();
        prestation.setTitreService("Visite Guidée"); // Rempli pour éviter @NotBlank
        prestation.setDescription("Description de test pour la prestation"); // Rempli pour éviter @NotBlank
        prestation.setPrixBase(99.99); // Rempli pour éviter @NotNull
        prestation.setPole(pole);
        
        entityManager.persist(prestation);
        entityManager.flush();

        // 3. Préparation : Créer la session associée
        Session session = Session.builder()
                .prestation(prestation)
                .dateDebut(LocalDateTime.now().plusDays(1))
                .dateFin(LocalDateTime.now().plusDays(5))
                .capaciteMax(10)
                .nbInscrits(0)
                .statutSession(StatutSession.CLOTURE)
                .build();
        
        entityManager.persist(session);
        entityManager.flush(); 

        // 4. Test de la transition
        mockMvc.perform(post("/api/sessions/{id}/transition", session.getId())
                .param("nouveauStatut", "OUVERT")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}