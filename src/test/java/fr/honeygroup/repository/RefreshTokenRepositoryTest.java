package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.RefreshToken;
import fr.honeygroup.bo.User;

@DataJpaTest
@DisplayName("Tests du repository RefreshTokenRepository")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Requête : Trouver un jeton par sa valeur textuelle")
    void findByToken_ShouldReturnToken_WhenExists() {
        String tokenValue = "refresh-uuid-12345";
        User user = RepositoryTestHelper.persistValidUser(entityManager, "user1@honeygroup.fr");
        RefreshToken token = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7).toInstant(java.time.ZoneOffset.UTC))
                .build();
        entityManager.persist(token);

        Optional<RefreshToken> result = refreshTokenRepository.findByToken(tokenValue);

        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo(tokenValue);
    }

    @Test
    @DisplayName("Requête : Suppression des jetons par utilisateur avec @Modifying")
    void deleteByUser_ShouldRemoveAllTokensForUser() {
        // 1. Préparation
        User user = RepositoryTestHelper.persistValidUser(entityManager, "user2@honeygroup.fr");

        RefreshToken t1 = RefreshToken.builder()
                .user(user)
                .token("token-1")
                .expiryDate(LocalDateTime.now().plusDays(7).toInstant(java.time.ZoneOffset.UTC))
                .build();
        entityManager.persist(t1);

        // 2. Action : suppression via repository
        refreshTokenRepository.deleteByUser(user);
        
        // 3. Vérification
        // Utilisation de flush pour s'assurer que la requête de suppression est exécutée
        entityManager.flush();
        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }
}