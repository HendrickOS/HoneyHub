package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.User;
import fr.honeygroup.enumeration.Role;

@DataJpaTest
@DisplayName("Tests du repository UserRepository")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Authentification : Trouver par email")
    void findByEmail_ShouldReturnUser() {
        User user = new User();
        user.setEmail("admin@honeygroup.fr");
        entityManager.persist(user);

        Optional<User> result = userRepository.findByEmail("admin@honeygroup.fr");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("admin@honeygroup.fr");
    }

    @Test
    @DisplayName("Sécurité : Vérifier existence par email")
    void existsByEmail_ShouldReturnTrue_WhenExists() {
        User user = new User();
        user.setEmail("test@honeygroup.fr");
        entityManager.persist(user);

        assertThat(userRepository.existsByEmail("test@honeygroup.fr")).isTrue();
        assertThat(userRepository.existsByEmail("nouveau@honeygroup.fr")).isFalse();
    }

    @Test
    @DisplayName("Recherche : Filtre flou par nom ou prénom")
    void findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase_ShouldReturnMatches() {
        User u1 = new User();
        u1.setNom("Dupont");
        u1.setPrenom("Jean");
        entityManager.persist(u1);

        List<User> result = userRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase("pon", "ea");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Dupont");
    }

    @Test
    @DisplayName("Administration : Filtrer par rôle")
    void findByRole_ShouldReturnUsersWithRole() {
        User admin = new User();
        admin.setRole(Role.ADMIN);
        entityManager.persist(admin);

        List<User> result = userRepository.findByRole(Role.ADMIN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(Role.ADMIN);
    }
}