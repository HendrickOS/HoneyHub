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
        User user = new User();
        user.setEmail("test@honeygroup.fr");
        entityManager.persist(user);

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setTelephone("0102030405");
        entityManager.persist(profile);

        // Exécution
        Optional<Profile> result = profileRepository.findByUserId(user.getId());

        // Vérification
        assertThat(result).isPresent();
        assertThat(result.get().getTelephone()).isEqualTo("0102030405");
        assertThat(result.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Existence : Contrôle d'unicité du téléphone")
    void existsByTelephone_ShouldReturnTrue_WhenExists() {
        Profile profile = new Profile();
        profile.setTelephone("0600000000");
        entityManager.persist(profile);

        boolean exists = profileRepository.existsByTelephone("0600000000");
        boolean notExists = profileRepository.existsByTelephone("0700000000");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}