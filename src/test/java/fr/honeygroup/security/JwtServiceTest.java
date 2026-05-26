package fr.honeygroup.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("Tests du service de sécurité JwtService")
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Injection manuelle de la configuration (Clé Base64 valide)
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 24h
    }

    @Test
    @DisplayName("JWT : Génération et extraction du nom d'utilisateur")
    void generateAndExtractUsername_Success() {
        UserDetails userDetails = new User("jean.dupont@honeygroup.fr", "password", Collections.emptyList());
        
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertNotNull(token);
        assertEquals("jean.dupont@honeygroup.fr", username);
    }

    @Test
    @DisplayName("JWT : Validation d'un jeton valide")
    void isTokenValid_ShouldReturnTrue() {
        UserDetails userDetails = new User("jean.dupont@honeygroup.fr", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }
}