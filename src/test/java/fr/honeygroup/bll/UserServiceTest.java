package fr.honeygroup.bll;

import static org.assertj.core.api.Assertions.assertThat;
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

import fr.honeygroup.bll.impl.UserServiceImpl;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.response.UserProfileResponse;
import fr.honeygroup.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Profil : Récupérer les données de l'utilisateur courant")
    void getCurrentUserProfile_ShouldReturnDto() {
        // 1. Préparation
        String email = "client@honeygroup.fr";
        User user = new User();
        user.setEmail(email);
        user.setNom("Doe");
        user.setPrenom("John");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // 2. Exécution
        UserProfileResponse response = userService.getCurrentUserProfile(email);

        // 3. Vérification
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getNom()).isEqualTo("Doe");
        verify(userRepository, times(1)).findByEmail(email);
    }
}