package fr.honeygroup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.honeygroup.bll.LeadService;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import fr.honeygroup.enumeration.StatutLead;

@WebMvcTest(LeadController.class)
public class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeadService leadService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createLead_ShouldReturn200AndLeadResponse() throws Exception {
        LeadRequest request = new LeadRequest();
        LeadResponse mockResponse = new LeadResponse();

        when(leadService.createLead(any(LeadRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getAllLeads_ShouldReturn200() throws Exception {
        when(leadService.getAllLeads()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getLeadById_ShouldReturn200() throws Exception {
        Long leadId = 1L;
        LeadResponse mockResponse = new LeadResponse();

        when(leadService.getLeadById(leadId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/leads/{id}", leadId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateLeadStatus_ShouldReturn200WithUpdatedLead() throws Exception {
        Long leadId = 1L;
        StatutLead nouveauStatut = StatutLead.EN_COURS; // Ajuste selon le nom exact de ton énumération
        LeadResponse mockResponse = new LeadResponse();

        when(leadService.updateLeadStatus(eq(leadId), any(StatutLead.class))).thenReturn(mockResponse);

        mockMvc.perform(put("/api/leads/{id}/status", leadId)
                .param("statut", nouveauStatut.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteLead_ShouldReturn200() throws Exception {
        Long leadId = 1L;
        doNothing().when(leadService).deleteLead(leadId);

        mockMvc.perform(delete("/api/leads/{id}", leadId))
                .andExpect(status().isOk());
    }
}