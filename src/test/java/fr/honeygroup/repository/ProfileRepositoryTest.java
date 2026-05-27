package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.Profile;
import fr.honeygroup.bo.User;

@DataJpaTest
@DisplayName("Tests du repository ProfileRepository")
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Requête : Trouver un profil par UserId")
    void findByUserId_ShouldReturnProfile() {
        // Préparation : User + Profile
        User user = RepositoryTestHelper.persistValidUser(entityManager, "test@honeygroup.fr");

        Profile profile = Profile.builder()
                .user(user)
                .adresse("123 Rue Principale")
                .telephone("+33102030405")
                .pays("France")
                .build();
        entityManager.persist(profile);

        // Exécution
        Optional<Profile> result = profileRepository.findByUserId(user.getId());

        // Vérification
        assertThat(result).isPresent();
        assertThat(result.get().getTelephone()).isEqualTo("+33102030405");
        assertThat(result.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Existence : Contrôle d'unicité du téléphone")
    void existsByTelephone_ShouldReturnTrue_WhenExists() {
        User user = RepositoryTestHelper.persistValidUser(entityManager, "test2@honeygroup.fr");
        Profile profile = Profile.builder()
                .user(user)
                .adresse("123 Rue Principale")
                .telephone("+33600000000")
                .pays("France")
                .build();
        entityManager.persist(profile);

        boolean exists = profileRepository.existsByTelephone("+33600000000");
        boolean notExists = profileRepository.existsByTelephone("+33700000000");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}