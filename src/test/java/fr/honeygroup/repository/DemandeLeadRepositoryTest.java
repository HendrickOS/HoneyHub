package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.User;
import fr.honeygroup.enumeration.StatutLead;

@DataJpaTest
@DisplayName("Tests du repository DemandeLeadRepository")
class DemandeLeadRepositoryTest {

    @Autowired
    private DemandeLeadRepository leadRepository;

    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @DisplayName("Requête : Trouver par Statut")
    void findByStatut_ShouldReturnMatchingLeads() {

        // Arrange
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");

        DemandeLead lead = DemandeLead.builder()
                .pole(pole)
                .source("WEB")
                .statut(StatutLead.NOUVEAU)
                .nomContact("Contact")
                .emailContact("contact@domain.com")
                .build();

        entityManager.persist(lead);
        entityManager.flush();
        entityManager.clear();

        // ⚠️ IMPORTANT : on passe le bon type attendu par le repository
        List<DemandeLead> result =
        		leadRepository.findByStatut(StatutLead.NOUVEAU);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatut())
                .isEqualTo(StatutLead.NOUVEAU);
    }

    

    @Test
    @DisplayName("Requête : Trouver par PoleId")
    void findByPoleId_ShouldReturnLeadsOfPole() {
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");

        DemandeLead lead = DemandeLead.builder()
                .pole(pole)
                .source("WEB")
                .statut(StatutLead.NOUVEAU)
                .nomContact("Contact")
                .emailContact("contact@domain.com")
                .build();
        entityManager.persist(lead);

        List<DemandeLead> result = leadRepository.findByPoleId(pole.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPole().getId()).isEqualTo(pole.getId());
    }

    @Test
    @DisplayName("Requête : Ordre chronologique descendant")
    void findAllByOrderByDateSoumissionDesc_ShouldReturnOrderedList() {
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");

        DemandeLead oldLead = DemandeLead.builder()
                .pole(pole)
                .source("WEB")
                .statut(StatutLead.NOUVEAU)
                .nomContact("Contact")
                .emailContact("contact@domain.com")
                .dateSoumission(LocalDateTime.now().minusDays(5))
                .build();
        entityManager.persist(oldLead);

        DemandeLead newLead = DemandeLead.builder()
                .pole(pole)
                .source("WEB")
                .statut(StatutLead.NOUVEAU)
                .nomContact("Contact")
                .emailContact("contact@domain.com")
                .dateSoumission(LocalDateTime.now())
                .build();
        entityManager.persist(newLead);

        List<DemandeLead> result = leadRepository.findAllByOrderByDateSoumissionDesc();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDateSoumission()).isAfter(result.get(1).getDateSoumission());
    }

    @Test
    @DisplayName("Requête : Trouver par UserId")
    void findByUserId_ShouldReturnUserLeads() {
        User user = RepositoryTestHelper.persistValidUser(entityManager, "test@honeygroup.fr");
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");

        DemandeLead lead = DemandeLead.builder()
                .user(user)
                .pole(pole)
                .source("WEB")
                .statut(StatutLead.NOUVEAU)
                .nomContact("Contact")
                .emailContact("contact@domain.com")
                .build();
        entityManager.persist(lead);

        List<DemandeLead> result = leadRepository.findByUserId(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getId()).isEqualTo(user.getId());
    }
}