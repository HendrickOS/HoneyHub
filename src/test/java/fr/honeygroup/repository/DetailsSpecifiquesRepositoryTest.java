package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.DetailsSpecifiques;

@DataJpaTest
@DisplayName("Tests du repository DetailsSpecifiquesRepository")
class DetailsSpecifiquesRepositoryTest {

    @Autowired
    private DetailsSpecifiquesRepository detailsRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Requête : Trouver tous les détails par ID de Lead")
    void findByDemandeLeadId_ShouldReturnDetailsForLead() {
        // 1. Préparation
        DemandeLead lead = new DemandeLead();
        entityManager.persist(lead);

        DetailsSpecifiques d1 = new DetailsSpecifiques();
        d1.setDemandeLead(lead);
        d1.setChampCle("Projet");
        d1.setValeur("Écotourisme");
        entityManager.persist(d1);

        DetailsSpecifiques d2 = new DetailsSpecifiques();
        d2.setDemandeLead(lead);
        d2.setChampCle("Budget");
        d2.setValeur("5000€");
        entityManager.persist(d2);

        // 2. Exécution
        List<DetailsSpecifiques> result = detailsRepository.findByDemandeLeadId(lead.getId());

        // 3. Vérification
        assertThat(result).hasSize(2);
        assertThat(result).extracting(DetailsSpecifiques::getChampCle)
                          .containsExactlyInAnyOrder("Projet", "Budget");
    }

    @Test
    @DisplayName("Requête : Trouver tous les détails par clé métier (champCle)")
    void findByChampCle_ShouldReturnMatchingDetails() {
        DetailsSpecifiques d1 = new DetailsSpecifiques();
        d1.setChampCle("Technologie");
        d1.setValeur("Java");
        entityManager.persist(d1);

        DetailsSpecifiques d2 = new DetailsSpecifiques();
        d2.setChampCle("Technologie");
        d2.setValeur("Python");
        entityManager.persist(d2);

        List<DetailsSpecifiques> result = detailsRepository.findByChampCle("Technologie");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getChampCle().equals("Technologie"));
    }
}