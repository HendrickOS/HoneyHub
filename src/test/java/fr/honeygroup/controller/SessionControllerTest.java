package fr.honeygroup.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import fr.honeygroup.bll.SessionService;
import fr.honeygroup.enumeration.StatutSession;
import fr.honeygroup.exception.GlobalExceptionHandler;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Devrait retourner 400 (Bad Request) ou 403 quand la transition est illégale")
    void shouldReturnErrorWhenTransitionIsIllegal() throws Exception {
        Long sessionId = 1L;
        StatutSession illegalStatut = StatutSession.OUVERT;

        // On simule une exception métier lors de la transition illégale
        doThrow(new GlobalExceptionHandler.BusinessSecurityException("Transition illégale"))
                .when(sessionService).transitionnerStatut(sessionId, illegalStatut);

        mockMvc.perform(post("/api/sessions/{id}/transition", sessionId)
                .param("nouveauStatut", illegalStatut.name())
                .with(csrf()))
                .andExpect(status().isForbidden()); // Adapte selon si tu renvoies 403 ou 400 dans ton ExceptionHandler
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Devrait retourner 403 si un simple utilisateur tente de changer le statut")
    void shouldReturnForbiddenForUnauthorizedUser() throws Exception {
        mockMvc.perform(post("/api/sessions/1/transition")
                .param("nouveauStatut", "CLOTURE")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}