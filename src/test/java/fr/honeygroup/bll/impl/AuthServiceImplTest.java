package fr.honeygroup.bll.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import fr.honeygroup.bo.PasswordResetToken;
import fr.honeygroup.bo.RefreshToken;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.ForgotPasswordRequest;
import fr.honeygroup.bo.request.LoginRequest;
import fr.honeygroup.bo.request.RefreshTokenRequest;
import fr.honeygroup.bo.request.RegisterRequest;
import fr.honeygroup.bo.request.ResetPasswordRequest;
import fr.honeygroup.bo.response.TokenResponse;
import fr.honeygroup.enumeration.Role;
import fr.honeygroup.repository.PasswordResetTokenRepository;
import fr.honeygroup.repository.ProfileRepository;
import fr.honeygroup.repository.RefreshTokenRepository;
import fr.honeygroup.repository.UserRepository;
import fr.honeygroup.security.JwtService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests des services d'authentification et de sécurité (IAM)")
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private ProfileRepository profileRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequestValide;
    private User userMock;

    @BeforeEach
    void setUp() {
        registerRequestValide = new RegisterRequest();
        registerRequestValide.setEmail("client1@honeygroup.fr");
        registerRequestValide.setTelephone("0102030405");
        registerRequestValide.setPassword("Secret123!");
        registerRequestValide.setNom("nomClient1");
        registerRequestValide.setPrenom("prenomClient1");

        userMock = User.builder()
                .id(1L)
                .email("client1@honeygroup.fr")
                .password("encodedPassword")
                .role(Role.CLIENT)
                .build();
    }

    @Test
    @DisplayName("Inscription : Succès de la création d'un compte avec hachage du mot de passe")
    void register_Succes() {
        when(userRepository.existsByEmail("client1@honeygroup.fr")).thenReturn(false);
        when(profileRepository.existsByTelephone("0102030405")).thenReturn(false);
        when(passwordEncoder.encode("Secret123!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resultat = authService.register(registerRequestValide);

        assertNotNull(resultat);
        assertEquals("client1@honeygroup.fr", resultat.getEmail());
        assertEquals("encodedPassword", resultat.getPassword());
        assertEquals(Role.CLIENT, resultat.getRole(), "Le rôle par défaut appliqué doit être CLIENT.");
        assertNotNull(resultat.getProfile(), "Le graphe d'objets doit contenir le profil lié.");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Inscription : Échec immédiat si l'adresse email est déjà présente en base")
    void register_ErreurEmailExisteDeja() {
        when(userRepository.existsByEmail("client1@honeygroup.fr")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequestValide);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Inscription : Échec immédiat si le numéro de téléphone est déjà utilisé")
    void register_ErreurTelephoneExisteDeja() {
        when(userRepository.existsByEmail("client1@honeygroup.fr")).thenReturn(false);
        when(profileRepository.existsByTelephone("0102030405")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequestValide);
        });

        assertEquals("Telephone already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Connexion : Authentification réussie et émission du couple de tokens JWT")
    void login_Succes() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("client1@honeygroup.fr");
        loginRequest.setPassword("Secret123!");

        when(userRepository.findByEmail("client1@honeygroup.fr")).thenReturn(Optional.of(userMock));
        when(jwtService.generateToken(userMock)).thenReturn("mocked-access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TokenResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mocked-access-token", response.getAccessToken());
        assertNotNull(response.getRefreshToken(), "Un refresh token de type UUID doit être généré.");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(refreshTokenRepository, times(1)).deleteByUser(userMock);
    }

    @Test
    @DisplayName("Deconnexion : Purge transactionnelle du refresh token si l'utilisateur existe")
    void logout_Succes() {
        when(userRepository.findByEmail("client1@honeygroup.fr")).thenReturn(Optional.of(userMock));

        authService.logout("client1@honeygroup.fr");

        verify(refreshTokenRepository, times(1)).deleteByUser(userMock);
    }

    @Test
    @DisplayName("Rafraîchissement : Génération d'une nouvelle session si le refresh token est valide")
    void refreshToken_Succes() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("valid-uuid-token");

        RefreshToken storedToken = RefreshToken.builder()
                .token("valid-uuid-token")
                .user(userMock)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(refreshTokenRepository.findByToken("valid-uuid-token")).thenReturn(Optional.of(storedToken));
        when(jwtService.generateToken(userMock)).thenReturn("new-access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TokenResponse response = authService.refreshToken(refreshRequest);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        verify(refreshTokenRepository, times(1)).findByToken("valid-uuid-token");
    }

    @Test
    @DisplayName("Rafraîchissement : Échec et purge de la base de données si le token soumis est expiré")
    void refreshToken_ErreurTokenExpire() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("expired-uuid-token");

        RefreshToken storedToken = RefreshToken.builder()
                .token("expired-uuid-token")
                .user(userMock)
                .expiryDate(Instant.now().minusSeconds(10))
                .build();

        when(refreshTokenRepository.findByToken("expired-uuid-token")).thenReturn(Optional.of(storedToken));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.refreshToken(refreshRequest);
        });

        assertTrue(exception.getMessage().contains("expired"));
        verify(refreshTokenRepository, times(1)).delete(storedToken);
    }

    @Test
    @DisplayName("Mot de passe oublié : Génération d'un token d'une heure et purge des précédents")
    void forgotPassword_Succes() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("client1@honeygroup.fr");

        when(userRepository.findByEmail("client1@honeygroup.fr")).thenReturn(Optional.of(userMock));

        authService.forgotPassword(request);

        verify(passwordResetTokenRepository, times(1)).deleteByUser(userMock);
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Réinitialisation mot de passe : Succès de la modification si le jeton d'oubli est valide")
    void resetPassword_Succes() {
        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setToken("reset-uuid-token");
        resetRequest.setNewPassword("NewSecret789!");

        PasswordResetToken storedResetToken = PasswordResetToken.builder()
                .token("reset-uuid-token")
                .user(userMock)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        when(passwordResetTokenRepository.findByToken("reset-uuid-token")).thenReturn(Optional.of(storedResetToken));
        when(passwordEncoder.encode("NewSecret789!")).thenReturn("newEncodedPassword");

        authService.resetPassword(resetRequest);

        assertEquals("newEncodedPassword", userMock.getPassword(), "L'empreinte du mot de passe doit être modifiée.");
        verify(userRepository, times(1)).save(userMock);
        verify(passwordResetTokenRepository, times(1)).delete(storedResetToken);
        verify(refreshTokenRepository, times(1)).deleteByUser(userMock);
    }

    @Test
    @DisplayName("Réinitialisation mot de passe : Échec si le token de réinitialisation a expiré")
    void resetPassword_ErreurTokenExpire() {
        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setToken("expired-reset-token");
        resetRequest.setNewPassword("NewSecret789!");

        PasswordResetToken expiredResetToken = PasswordResetToken.builder()
                .token("expired-reset-token")
                .user(userMock)
                .expiryDate(LocalDateTime.now().minusMinutes(5)) // Expiré il y a 5 min
                .build();

        when(passwordResetTokenRepository.findByToken("expired-reset-token")).thenReturn(Optional.of(expiredResetToken));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.resetPassword(resetRequest);
        });

        assertEquals("Token has expired", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}