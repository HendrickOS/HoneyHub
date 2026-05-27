package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.enumeration.StatutSession;

@DataJpaTest
@DisplayName("Tests du repository SessionRepository")
class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Requête JPQL : Trouver uniquement les sessions disponibles")
    void findAvailableSessionsByPrestationId_ShouldFilterCorrectly() {
        // 1. Préparation
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");
        Prestation prestation = RepositoryTestHelper.persistValidPrestation(entityManager, pole, "Safari");

        LocalDateTime now = LocalDateTime.now();

        // Session valide
        Session s1 = Session.builder()
                .prestation(prestation)
                .dateDebut(now.plusDays(2))
                .dateFin(now.plusDays(5))
                .nbInscrits(5)
                .capaciteMax(10)
                .statutSession(StatutSession.OUVERT)
                .build();
        entityManager.persist(s1);

        // Session expirée (date passée)
        Session s2 = Session.builder()
                .prestation(prestation)
                .dateDebut(now.minusDays(5))
                .dateFin(now.minusDays(1))
                .nbInscrits(5)
                .capaciteMax(10)
                .statutSession(StatutSession.OUVERT)
                .build();
        entityManager.persist(s2);

        // Session pleine
        Session s3 = Session.builder()
                .prestation(prestation)
                .dateDebut(now.plusDays(2))
                .dateFin(now.plusDays(5))
                .nbInscrits(10)
                .capaciteMax(10)
                .statutSession(StatutSession.OUVERT)
                .build();
        entityManager.persist(s3);

        // 2. Exécution
        List<Session> available = sessionRepository.findAvailableSessionsByPrestationId(
            prestation.getId(), now, StatutSession.OUVERT
        );

        // 3. Vérification
        assertThat(available).hasSize(1);
        assertThat(available.get(0).getId()).isEqualTo(s1.getId());
    }
}