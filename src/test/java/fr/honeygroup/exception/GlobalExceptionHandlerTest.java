package fr.honeygroup.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@DisplayName("Tests du GlobalExceptionHandler (Gestion des erreurs)")
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    // Contrôleur fictif pour déclencher les exceptions lors des tests
    @RestController
    static class TestController {
        @GetMapping("/test-bad-credentials")
        public void throwBadCredentials() { throw new BadCredentialsException("Erreur"); }

        @GetMapping("/test-business-error")
        public void throwBusinessError() { throw new GlobalExceptionHandler.BusinessLogicException("Article introuvable"); }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("401 : BadCredentialsException est convertie en Unauthorized")
    void handleBadCredentials_Returns401() throws Exception {
        mockMvc.perform(get("/test-bad-credentials").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email ou mot de passe incorrect"))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("404 : BusinessLogicException avec 'introuvable' retourne Not Found")
    void handleBusinessLogic_Returns404() throws Exception {
        mockMvc.perform(get("/test-business-error").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Article introuvable"));
    }
}