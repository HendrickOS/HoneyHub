package fr.honeygroup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.honeygroup.bll.AuthService;
import fr.honeygroup.bo.request.ForgotPasswordRequest;
import fr.honeygroup.bo.request.LoginRequest;
import fr.honeygroup.bo.request.RefreshTokenRequest;
import fr.honeygroup.bo.request.RegisterRequest;
import fr.honeygroup.bo.request.ResetPasswordRequest;
import fr.honeygroup.bo.response.TokenResponse;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void register_ShouldReturn200AndSuccessMessage() throws Exception {
        RegisterRequest request = new RegisterRequest();
        doNothing().when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    @WithMockUser
    void login_ShouldReturn200AndTokenResponse() throws Exception {
        LoginRequest request = new LoginRequest();
        TokenResponse mockResponse = new TokenResponse();

        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void refresh_ShouldReturn200AndUpdatedTokens() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        TokenResponse mockResponse = new TokenResponse();

        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser@honeygroup.fr")
    void logout_ShouldReturn200WhenAuthenticated() throws Exception {
        doNothing().when(authService).logout("testuser@honeygroup.fr");

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void forgotPassword_ShouldReturn200() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        doNothing().when(authService).forgotPassword(any(ForgotPasswordRequest.class));

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void resetPassword_ShouldReturn200() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        doNothing().when(authService).resetPassword(any(ResetPasswordRequest.class));

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}