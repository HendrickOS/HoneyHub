package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.PasswordResetToken;
import fr.honeygroup.bo.User;

@DataJpaTest
@DisplayName("Tests du repository PasswordResetTokenRepository")
class PasswordResetTokenRepositoryTest {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Requête : Trouver un jeton par sa valeur textuelle")
    void findByToken_ShouldReturnToken_WhenExists() {
        String tokenValue = "abc-123-token";
        User user = RepositoryTestHelper.persistValidUser(entityManager, "user1@honeygroup.fr");
        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        entityManager.persist(token);

        Optional<PasswordResetToken> result = tokenRepository.findByToken(tokenValue);

        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo(tokenValue);
    }

    @Test
    @DisplayName("Requête : Suppression des jetons par utilisateur")
    void deleteByUser_ShouldRemoveAllTokensForUser() {
        User user = RepositoryTestHelper.persistValidUser(entityManager, "user2@honeygroup.fr");

        PasswordResetToken t1 = PasswordResetToken.builder()
                .user(user)
                .token("token-1")
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        entityManager.persist(t1);

        // Exécution de la suppression
        tokenRepository.deleteByUser(user);
        entityManager.flush(); // Force la synchronisation en base

        // Vérification
        assertThat(tokenRepository.findAll()).isEmpty();
    }
}