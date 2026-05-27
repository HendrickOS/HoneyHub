package fr.honeygroup.bll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import fr.honeygroup.bll.impl.AuthServiceImpl;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.RegisterRequest;
import fr.honeygroup.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Inscription : Créer un utilisateur et hacher le mot de passe")
    void register_ShouldSaveEncodedUser() {
        // 1. Préparation
        RegisterRequest request = new RegisterRequest();
        request.setEmail("nouveau@honeygroup.fr");
        request.setPassword("Password123!");
        
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. Exécution
        User result = authService.register(request);

        // 3. Vérification
        assertThat(result.getEmail()).isEqualTo("nouveau@honeygroup.fr");
        assertThat(result.getPassword()).isEqualTo("hashed_password");
        verify(userRepository, times(1)).save(any(User.class));
    }
}