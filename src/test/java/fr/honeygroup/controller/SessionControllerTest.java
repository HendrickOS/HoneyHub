package fr.honeygroup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.honeygroup.bll.SessionService;
import fr.honeygroup.bo.request.SessionRequest;
import fr.honeygroup.bo.response.SessionResponse;
import fr.honeygroup.enumeration.StatutSession;

@WebMvcTest(SessionController.class)
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN") // Requis par le @PreAuthorize
    void createSession_ShouldReturn201Created() throws Exception {
        SessionRequest request = new SessionRequest();
        SessionResponse mockResponse = new SessionResponse();

        when(sessionService.createSession(any(SessionRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Vérifie le statut 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateSession_ShouldReturn200Ok() throws Exception {
        Long sessionId = 1L;
        SessionRequest request = new SessionRequest();
        SessionResponse mockResponse = new SessionResponse();

        when(sessionService.updateSession(eq(sessionId), any(SessionRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(put("/api/sessions/{id}", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser // Accès public ou utilisateur basique authentifié
    void getSession_ShouldReturn200Ok() throws Exception {
        Long sessionId = 1L;
        SessionResponse mockResponse = new SessionResponse();

        when(sessionService.getSessionDetails(sessionId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/sessions/{id}", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void transitionnerStatut_ShouldReturn200AndStringMessage() throws Exception {
        Long sessionId = 1L;
        StatutSession nouveauStatut = StatutSession.OUVERT; 
        String ancienStatutSimule = "BROUILLON";

        when(sessionService.transitionnerStatut(eq(sessionId), any(StatutSession.class)))
                .thenReturn(ancienStatutSimule);

        String messageAttendu = String.format("Succès : Statut de la session %d mis à jour de %s vers %s.", 
                sessionId, ancienStatutSimule, nouveauStatut.name());

        mockMvc.perform(post("/api/sessions/{id}/transition", sessionId)
                .param("nouveauStatut", nouveauStatut.name()))
                .andExpect(status().isOk())
                .andExpect(content().string(messageAttendu));
    }
}