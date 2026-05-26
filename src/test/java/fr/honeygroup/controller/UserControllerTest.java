package fr.honeygroup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import fr.honeygroup.bll.UserService;
import fr.honeygroup.bo.request.ProfileUpdateRequest;
import fr.honeygroup.bo.response.UserProfileResponse;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user@honeygroup.fr") // Injecte l'identité requise par authentication.getName()
    void getCurrentUser_ShouldReturn200AndProfile() throws Exception {
        String email = "user@honeygroup.fr";
        UserProfileResponse mockResponse = new UserProfileResponse(); // S'assurer du @NoArgsConstructor

        when(userService.getCurrentUserProfile(email)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "user@honeygroup.fr")
    void updateProfile_ShouldReturn200AndUpdatedProfile() throws Exception {
        String email = "user@honeygroup.fr";
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        UserProfileResponse mockResponse = new UserProfileResponse();

        when(userService.updateProfile(eq(email), any(ProfileUpdateRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(put("/api/users/me/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}