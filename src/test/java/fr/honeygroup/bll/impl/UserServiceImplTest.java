package fr.honeygroup.bll.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import enumeration.Role;
import fr.honeygroup.bo.Profile;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.ProfileUpdateRequest;
import fr.honeygroup.bo.response.UserProfileResponse;
import fr.honeygroup.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests des règles métier de UserService (BLL)")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User userMock;
    private Profile profileMock;
    private ProfileUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        profileMock = Profile.builder()
                .id(1L)
                .telephone("0102030405")
                .adresse("10 Rue Principale")
                .pays("France")
                .preferences("Nature, Ecotourisme")
                .build();

        userMock = User.builder()
                .id(100L)
                .email("client1@honeygroup.fr")
                .nom("nomClient1")
                .prenom("prenomClient1")
                .role(Role.CLIENT)
                .profile(profileMock)
                .build();

        profileMock.setUser(userMock);

        updateRequest = new ProfileUpdateRequest();
        updateRequest.setNom("NouveauNom");
        updateRequest.setPrenom("NouveauPrenom");
        updateRequest.setTelephone("0607080910");
        updateRequest.setAdresse("20 Nouvelle Rue");
        updateRequest.setPays("Belgique");
        updateRequest.setPreferences("Aventure");
    }

    @Test
    @DisplayName("Récupération profil : Succès et conversion complète en DTO")
    void getCurrentUserProfile_Succes() {
        // ARRANGE
        when(userRepository.findByEmail("client1@honeygroup.fr")).thenReturn(Optional.of(userMock));

        // ACT
        UserProfileResponse response = userService.getCurrentUserProfile("client1@honeygroup.fr");

        // ASSERT
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("client1@honeygroup.fr", response.getEmail());
        assertEquals("nomClient1", response.getNom());
        assertEquals("0102030405", response.getTelephone());
        verify(userRepository, times(1)).findByEmail("client1@honeygroup.fr");
    }

    @Test
    @DisplayName("Récupération profil : Échec si l'utilisateur n'existe pas")
    void getCurrentUserProfile_ErreurUserIntrouvable() {
        // ARRANGE
        when(userRepository.findByEmail("inconnu@honeygroup.fr")).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getCurrentUserProfile("inconnu@honeygroup.fr");
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Mise à jour profil : Succès avec modification complète des données et du profil existant")
    void updateProfile_SuccesProfilExistant() {
        // ARRANGE
        when(userRepository.findByEmail("client1@honeygroup.fr")).thenReturn(Optional.of(userMock));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        UserProfileResponse response = userService.updateProfile("client1@honeygroup.fr", updateRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals("NouveauNom", response.getNom());
        assertEquals("NouveauPrenom", response.getPrenom());
        assertEquals("0607080910", response.getTelephone());
        assertEquals("20 Nouvelle Rue", response.getAdresse());
        assertEquals("Belgique", response.getPays());
        assertEquals("Aventure", response.getPreferences());
        verify(userRepository, times(1)).save(userMock);
    }

    @Test
    @DisplayName("Mise à jour profil : Succès et initialisation à la volée d'un profil null (Orphelin)")
    void updateProfile_SuccesProfilNullInitialise() {
        // ARRANGE
        userMock.setProfile(null); // On simule un utilisateur sans profil en base
        when(userRepository.findByEmail("client1@honeygroup.fr")).thenReturn(Optional.of(userMock));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        UserProfileResponse response = userService.updateProfile("client1@honeygroup.fr", updateRequest);

        // ASSERT
        assertNotNull(response);
        assertNotNull(userMock.getProfile(), "Le profil orphelin doit avoir été initialisé à la volée.");
        assertEquals("0607080910", response.getTelephone());
        assertEquals("Belgique", response.getPays());
        verify(userRepository, times(1)).save(userMock);
    }
}