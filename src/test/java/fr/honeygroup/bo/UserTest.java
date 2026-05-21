package fr.honeygroup.bo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import enumeration.Role;

@DisplayName("Tests unitaires de l'entité User (Sécurité & Profil)")
class UserTest {

    @Test
    @DisplayName("Vérification que le Builder Lombok mappe correctement les données civiles et de connexion")
    void builder_DevraitConstruireLObjetCorrectement() {
        // ARRANGE & ACT
        User user = User.builder()
                .id(1L)
                .email("test@honeygroup.fr")
                .password("Chiffre_BCrypt_123")
                .nom("Osseux")
                .prenom("Hendrick")
                .role(Role.CLIENT)
                .build();

        // ASSERT
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("test@honeygroup.fr", user.getEmail());
        assertEquals("Chiffre_BCrypt_123", user.getPassword());
        assertEquals("Osseux", user.getNom());
        assertEquals("Hendrick", user.getPrenom());
        assertEquals(Role.CLIENT, user.getRole());
    }

    @Test
    @DisplayName("Vérification de la conversion du rôle métier en GrantedAuthority Spring Security (avec préfixe ROLE_)")
    void getAuthorities_DevraitRetournerLeRoleFormatePourSpringSecurity() {
        // ARRANGE
        User user = User.builder()
                .role(Role.MANAGER)
                .build();

        // ACT
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // ASSERT
        assertNotNull(authorities);
        assertEquals(1, authorities.size(), "L'utilisateur doit posséder exactement une habilitation.");
        
        String expectedAuthority = "ROLE_MANAGER";
        String actualAuthority = authorities.iterator().next().getAuthority();
        assertEquals(expectedAuthority, actualAuthority, "L'autorité générée doit être préfixée par 'ROLE_'.");
    }

    @Test
    @DisplayName("Vérification que le Username requis par Spring Security renvoie bien l'adresse Email")
    void getUsername_DevraitRetournerLEmail() {
        // ARRANGE
        User user = User.builder()
                .email("contact@honeygroup.com")
                .build();

        // ACT & ASSERT
        assertEquals("contact@honeygroup.com", user.getUsername(), "Le username doit correspondre à l'email de l'utilisateur.");
    }

    @Test
    @DisplayName("Vérification que les indicateurs d'état du compte Spring Security sont tous activés par défaut")
    void staticFlagsSpringSecurity_DevraientTousEtreTrue() {
        // ARRANGE
        User user = new User();

        // ACT & ASSERT
        assertTrue(user.isAccountNonExpired(), "Le compte ne doit pas expirer.");
        assertTrue(user.isAccountNonLocked(), "Le compte ne doit pas être verrouillé.");
        assertTrue(user.isCredentialsNonExpired(), "Les identifiants ne doivent pas expirer.");
        assertTrue(user.isEnabled(), "L'utilisateur doit être activé par défaut.");
    }
}