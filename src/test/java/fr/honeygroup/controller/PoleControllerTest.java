package fr.honeygroup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import fr.honeygroup.bll.PoleService;
import fr.honeygroup.bo.request.PoleRequest;
import fr.honeygroup.bo.response.PoleResponse;

@WebMvcTest(PoleController.class)
@org.springframework.context.annotation.Import(ControllerTestConfig.class)
public class PoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PoleService poleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void create_ShouldReturn200AndPoleResponse() throws Exception {
        PoleRequest request = new PoleRequest();
        request.setNom("Ecotourisme");
        request.setDescription("Description du pole d'ecotourisme de Honey Group");
        
        PoleResponse mockResponse = new PoleResponse();

        when(poleService.create(any(PoleRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/poles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturn200AndList() throws Exception {
        when(poleService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/poles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void getById_ShouldReturn200AndPoleResponse() throws Exception {
        Long poleId = 1L;
        PoleResponse mockResponse = new PoleResponse();

        when(poleService.getById(poleId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/poles/{id}", poleId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void getByNom_ShouldReturn200UsingQueryParam() throws Exception {
        String nomPole = "Ecotourisme";
        PoleResponse mockResponse = new PoleResponse();

        when(poleService.getByNom(eq(nomPole))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/poles/search")
                .param("nom", nomPole))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Anticipation de la sécurité sur la suppression
    void delete_ShouldReturn200() throws Exception {
        Long poleId = 1L;
        doNothing().when(poleService).deleteById(poleId);

        mockMvc.perform(delete("/api/poles/{id}", poleId))
                .andExpect(status().isOk());
    }
}