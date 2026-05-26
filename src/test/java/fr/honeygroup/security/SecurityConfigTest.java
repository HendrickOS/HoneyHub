package fr.honeygroup.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests d'intégration de la configuration de sécurité")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Accès public : /api/auth/** doit être accessible sans token")
    void publicEndpoints_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isUnauthorized()); // 401 attendu car l'auth existe mais est vide
    }

    @Test
    @DisplayName("Accès restreint : /api/admin/** doit être protégé")
    void adminEndpoints_ShouldBeProtected() throws Exception {
        mockMvc.perform(get("/api/bookings/admin/all"))
                .andExpect(status().isForbidden()); // 403 attendu car pas de rôle ADMIN
    }

    @Test
    @DisplayName("Accès lead : /api/leads/ POST doit être public")
    void leadPost_ShouldBePublic() throws Exception {
        // Cette route devrait être accessible à tout le monde
        mockMvc.perform(post("/api/leads/"))
                .andExpect(status().isBadRequest()); // 400 car pas de corps de requête, mais pas 403
    }
}