package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
        DemandeLead lead = new DemandeLead();
        lead.setStatut(StatutLead.NOUVEAU);
        entityManager.persist(lead);

        List<DemandeLead> result = leadRepository.findByStatut("NOUVEAU");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatut()).isEqualTo("NOUVEAU");
    }

    @Test
    @DisplayName("Requête : Trouver par PoleId")
    void findByPoleId_ShouldReturnLeadsOfPole() {
        Pole pole = new Pole();
        entityManager.persist(pole);

        DemandeLead lead = new DemandeLead();
        lead.setPole(pole);
        entityManager.persist(lead);

        List<DemandeLead> result = leadRepository.findByPoleId(pole.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPole().getId()).isEqualTo(pole.getId());
    }

    @Test
    @DisplayName("Requête : Ordre chronologique descendant")
    void findAllByOrderByDateSoumissionDesc_ShouldReturnOrderedList() {
        DemandeLead oldLead = new DemandeLead();
        oldLead.setDateSoumission(LocalDateTime.now().minusDays(5));
        entityManager.persist(oldLead);

        DemandeLead newLead = new DemandeLead();
        newLead.setDateSoumission(LocalDateTime.now());
        entityManager.persist(newLead);

        List<DemandeLead> result = leadRepository.findAllByOrderByDateSoumissionDesc();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDateSoumission()).isAfter(result.get(1).getDateSoumission());
    }

    @Test
    @DisplayName("Requête : Trouver par UserId")
    void findByUserId_ShouldReturnUserLeads() {
        User user = new User();
        entityManager.persist(user);

        DemandeLead lead = new DemandeLead();
        lead.setUser(user);
        entityManager.persist(lead);

        List<DemandeLead> result = leadRepository.findByUserId(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getId()).isEqualTo(user.getId());
    }
}