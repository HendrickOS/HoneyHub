package fr.honeygroup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.honeygroup.bll.BookingService;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;

@WebMvcTest(BookingController.class)
@org.springframework.context.annotation.Import(ControllerTestConfig.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void creerReservation_ShouldReturn201() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setSessionId(1L);
        request.setNbPersonnes(2);
        request.setTypeReservation(fr.honeygroup.enumeration.TypeReservation.SESSION);

        BookingResponse response = new BookingResponse();

        when(bookingService.creerReservationSandbox(any(BookingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/bookings/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getMonHistorique_ShouldReturn200() throws Exception {
        when(bookingService.getUtilisateurHistoriquePersonnel()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bookings/my-bookings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}