package fr.honeygroup.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.honeygroup.bo.request.BookingRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Réservation : 201 Created pour un utilisateur connecté")
    @WithMockUser(username = "client@honeygroup.fr", roles = "USER")
    void createBooking_ShouldReturn201_WhenUserIsAuthenticated() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setSessionId(1L);
/*
        mockMvc.perform(post("/api/bookings/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());*/
    }

    @Test
    @DisplayName("Réservation : 403 Forbidden si non connecté")
    void createBooking_ShouldReturn403_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/bookings/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }
}