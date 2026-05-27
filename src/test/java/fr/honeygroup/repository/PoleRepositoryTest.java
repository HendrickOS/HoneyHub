package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.Pole;

@DataJpaTest
@DisplayName("Tests du repository PoleRepository")
class PoleRepositoryTest {

    @Autowired
    private PoleRepository poleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Existence : Contrôle des doublons avec existsByNom")
    void existsByNom_ShouldReturnTrue_WhenExists() {
        Pole pole = new Pole();
        pole.setNom("Écotourisme");
        entityManager.persist(pole);

        boolean exists = poleRepository.existsByNom("Écotourisme");
        boolean notExists = poleRepository.existsByNom("Inexistant");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Recherche : Trouver par nom exact")
    void findByNom_ShouldReturnPole() {
        Pole pole = new Pole();
        pole.setNom("IT Outsourcing");
        entityManager.persist(pole);

        Optional<Pole> result = poleRepository.findByNom("IT Outsourcing");

        assertThat(result).isPresent();
        assertThat(result.get().getNom()).isEqualTo("IT Outsourcing");
    }

    @Test
    @DisplayName("Tri : Ordre alphabétique croissant")
    void findAllByOrderByNomAsc_ShouldReturnSortedList() {
        Pole p1 = new Pole(); p1.setNom("Zéro"); entityManager.persist(p1);
        Pole p2 = new Pole(); p2.setNom("Alpha"); entityManager.persist(p2);

        List<Pole> result = poleRepository.findAllByOrderByNomAsc();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNom()).isEqualTo("Alpha");
        assertThat(result.get(1).getNom()).isEqualTo("Zéro");
    }

    @Test
    @DisplayName("Recherche : Correspondance partielle (Insensible à la casse)")
    void findByNomContainingIgnoreCase_ShouldFindMatch() {
        Pole pole = new Pole();
        pole.setNom("Développement Web");
        entityManager.persist(pole);

        List<Pole> result = poleRepository.findByNomContainingIgnoreCase("dev");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Développement Web");
    }
}