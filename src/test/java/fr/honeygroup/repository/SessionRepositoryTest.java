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
        Prestation prestation = new Prestation();
        entityManager.persist(prestation);

        LocalDateTime now = LocalDateTime.now();

        // Session valide
        Session s1 = new Session();
        s1.setPrestation(prestation);
        s1.setDateDebut(now.plusDays(1));
        s1.setNbInscrits(5);
        s1.setCapaciteMax(10);
        s1.setStatutSession(StatutSession.OUVERT);
        entityManager.persist(s1);

        // Session expirée (date passée)
        Session s2 = new Session();
        s2.setPrestation(prestation);
        s2.setDateDebut(now.minusDays(1));
        s2.setNbInscrits(5);
        s2.setCapaciteMax(10);
        s2.setStatutSession(StatutSession.OUVERT);
        entityManager.persist(s2);

        // Session pleine
        Session s3 = new Session();
        s3.setPrestation(prestation);
        s3.setDateDebut(now.plusDays(2));
        s3.setNbInscrits(10);
        s3.setCapaciteMax(10);
        s3.setStatutSession(StatutSession.OUVERT);
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