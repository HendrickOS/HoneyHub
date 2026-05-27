package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.Prestation;

@DataJpaTest
@DisplayName("Tests du repository PrestationRepository")
class PrestationRepositoryTest {

    @Autowired
    private PrestationRepository prestationRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Requête : Filtrage par prix entre deux bornes")
    void findByPrixBaseBetween_ShouldReturnInRange() {
        Prestation p1 = new Prestation();
        // Conversion de BigDecimal vers Double pour le setter
        p1.setPrixBase(new BigDecimal("100.00").doubleValue());
        entityManager.persist(p1);
        
        Prestation p2 = new Prestation();
        // Conversion de BigDecimal vers Double pour le setter
        p2.setPrixBase(new BigDecimal("500.00").doubleValue());
        entityManager.persist(p2);

        // Appel au repository (qui attend des BigDecimal d'après ton interface)
        List<Prestation> result = prestationRepository.findByPrixBaseBetween(
            new BigDecimal("50"), 
            new BigDecimal("150")
        );

        assertThat(result).hasSize(1);
        // Comparaison entre Double et Double (via conversion du String en Double)
        assertThat(result.get(0).getPrixBase()).isEqualTo(Double.valueOf("100.00"));
    }

    @Test
    @DisplayName("Requête native JSON : Recherche par lieu de départ")
    void findByLieuDepart_ShouldReturnMatchingMetadata() {
        Prestation p1 = new Prestation();
        
        // 1. Création de la Map pour respecter le typage de setMetadata
        Map<String, Object> meta = new HashMap<>();
        meta.put("lieu_depart", "Paris");
        meta.put("lieu_arrivee", "Lyon");
        p1.setMetadata(meta);
        
        entityManager.persist(p1);

        List<Prestation> result = prestationRepository.findByLieuDepart("Paris");

        assertThat(result).hasSize(1);
        // 2. Vérification sur la map retournée par le getter
        assertThat(result.get(0).getMetadata()).containsEntry("lieu_depart", "Paris");
    }

    @Test
    @DisplayName("Requête native JSON : Recherche par trajet complet")
    void findByTrajet_ShouldReturnMatchingMetadata() {
        Prestation p1 = new Prestation();
        
        Map<String, Object> meta = new HashMap<>();
        meta.put("lieu_depart", "Paris");
        meta.put("lieu_arrivee", "Lyon");
        p1.setMetadata(meta);
        
        entityManager.persist(p1);

        List<Prestation> result = prestationRepository.findByTrajet("Paris", "Lyon");

        assertThat(result).hasSize(1);
        // 3. Vérification des entrées de la map
        assertThat(result.get(0).getMetadata())
            .containsEntry("lieu_depart", "Paris")
            .containsEntry("lieu_arrivee", "Lyon");
    }
}