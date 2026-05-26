package fr.honeygroup.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import fr.honeygroup.bo.User; // Assure-toi que ton entité User implémente UserDetails
import fr.honeygroup.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service de sécurité CustomUserDetailsService")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Chargement utilisateur : Succès quand l'utilisateur existe")
    void loadUserByUsername_Succes() {
        User mockUser = new User();
        mockUser.setEmail("test@honeygroup.fr");
        
        when(userRepository.findByEmail("test@honeygroup.fr")).thenReturn(Optional.of(mockUser));

        var userDetails = userDetailsService.loadUserByUsername("test@honeygroup.fr");

        assertNotNull(userDetails);
        assertEquals("test@honeygroup.fr", userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail("test@honeygroup.fr");
    }

    @Test
    @DisplayName("Chargement utilisateur : Exception quand l'utilisateur n'existe pas")
    void loadUserByUsername_Echec() {
        when(userRepository.findByEmail("inconnu@honeygroup.fr")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("inconnu@honeygroup.fr");
        });
    }
}