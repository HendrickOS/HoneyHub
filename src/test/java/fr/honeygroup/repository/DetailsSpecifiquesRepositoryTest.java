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
import fr.honeygroup.bo.Pole;

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
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");
        DemandeLead lead = RepositoryTestHelper.persistValidDemandeLead(entityManager, pole, null);

        DetailsSpecifiques d1 = DetailsSpecifiques.builder()
                .demandeLead(lead)
                .champCle("Projet")
                .valeur("Écotourisme")
                .build();
        entityManager.persist(d1);

        DetailsSpecifiques d2 = DetailsSpecifiques.builder()
                .demandeLead(lead)
                .champCle("Budget")
                .valeur("5000€")
                .build();
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
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");
        DemandeLead lead = RepositoryTestHelper.persistValidDemandeLead(entityManager, pole, null);

        DetailsSpecifiques d1 = DetailsSpecifiques.builder()
                .demandeLead(lead)
                .champCle("Technologie")
                .valeur("Java")
                .build();
        entityManager.persist(d1);

        DetailsSpecifiques d2 = DetailsSpecifiques.builder()
                .demandeLead(lead)
                .champCle("Technologie")
                .valeur("Python")
                .build();
        entityManager.persist(d2);

        List<DetailsSpecifiques> result = detailsRepository.findByChampCle("Technologie");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getChampCle().equals("Technologie"));
    }
}