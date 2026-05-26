package fr.honeygroup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.honeygroup.bll.PrestationService;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.request.CircuitRequest;
import fr.honeygroup.bo.request.CoursLangueRequest;
import fr.honeygroup.bo.request.PrestationRequest;
import fr.honeygroup.bo.response.PrestationResponse;
import fr.honeygroup.mapper.PrestationMapper;
import fr.honeygroup.repository.PrestationRepository;

@WebMvcTest(PrestationController.class)
public class PrestationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PrestationService prestationService;

    @MockitoBean
    private PrestationRepository prestationRepository;

    @MockitoBean
    private PrestationMapper prestationMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllPrestations_ShouldReturn200() throws Exception {
        when(prestationService.getAllPrestations()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/prestations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void getPrestationById_ShouldReturn200() throws Exception {
        Long id = 1L;
        PrestationResponse mockResponse = new PrestationResponse();

        when(prestationService.getPrestationById(id)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/prestations/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPrestationGenerique_ShouldReturn200() throws Exception {
        PrestationRequest request = new PrestationRequest();
        PrestationResponse mockResponse = new PrestationResponse();

        when(prestationService.createPrestationGenerique(any(PrestationRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/prestations/generique")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCircuit_ShouldReturn200() throws Exception {
        CircuitRequest request = new CircuitRequest();
        PrestationResponse mockResponse = new PrestationResponse();

        when(prestationService.createCircuit(any(CircuitRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/prestations/circuit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCoursLangue_ShouldReturn200() throws Exception {
        CoursLangueRequest request = new CoursLangueRequest();
        PrestationResponse mockResponse = new PrestationResponse();

        when(prestationService.createCoursLangue(any(CoursLangueRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/prestations/courslangue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePrestation_ShouldReturn200() throws Exception {
        Long id = 1L;
        doNothing().when(prestationService).deletePrestation(id);

        mockMvc.perform(delete("/api/prestations/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateMetadata_ShouldReturn200WhenValid() throws Exception {
        Long id = 1L;
        Map<String, Object> metadata = Map.of("difficulte", "facile");

        doNothing().when(prestationService).addOrUpdateMetadata(eq(id), eq("difficulte"), eq("facile"));

        mockMvc.perform(patch("/api/prestations/{id}/metadata", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(metadata)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateMetadata_ShouldReturn400WhenEmpty() throws Exception {
        Long id = 1L;
        Map<String, Object> emptyMetadata = new HashMap<>();

        mockMvc.perform(patch("/api/prestations/{id}/metadata", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyMetadata)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser
    void getMetadata_ShouldReturn200AndMetadataMap() throws Exception {
        Long id = 1L;
        PrestationResponse mockResponse = new PrestationResponse();
        Map<String, Object> mockMetadata = Map.of("depart", "Paris");
        mockResponse.setMetadata(mockMetadata); // Assure-toi que le setter de metadata est disponible

        when(prestationService.getPrestationById(id)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/prestations/{id}/metadata", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.depart").value("Paris"));
    }

    @Test
    @WithMockUser
    void searchByLocation_ShouldFilterByDepart() throws Exception {
        String depart = "Nantes";
        Prestation mockPrestation = new Prestation();
        PrestationResponse mockResponse = new PrestationResponse();

        when(prestationRepository.findByLieuDepart(depart)).thenReturn(List.of(mockPrestation));
        when(prestationMapper.toGenericResponse(any(Prestation.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/prestations/search")
                .param("depart", depart))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getByTrajet_ShouldReturn200() throws Exception {
        String depart = "Nantes";
        String arrivee = "Brest";

        when(prestationService.findByTrajet(depart, arrivee)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/prestations/search/trajet")
                .param("depart", depart)
                .param("arrivee", arrivee))
                .andExpect(status().isOk());
    }
}