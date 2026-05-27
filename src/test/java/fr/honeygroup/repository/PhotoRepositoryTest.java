package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.Photo;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.Prestation;

@DataJpaTest
@DisplayName("Tests du repository PhotoRepository")
class PhotoRepositoryTest {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Requête : Trouver la photo par ID de prestation")
    void findByPrestation_Id_ShouldReturnPhoto() {
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");
        Photo photo = RepositoryTestHelper.persistValidPhoto(entityManager, "http://honeygroup.fr/photo.jpg");

        Prestation prestation = Prestation.builder()
                .pole(pole)
                .photo(photo)
                .titreService("Safari")
                .description("Description valide de plus de 10 caractères")
                .prixBase(100.0)
                .build();
        entityManager.persist(prestation);

        List<Photo> result = photoRepository.findByPrestation_Id(prestation.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUrlFichier()).isEqualTo("http://honeygroup.fr/photo.jpg");
    }

    @Test
    @DisplayName("Requête : Trouver toutes les photos d'un pôle")
    void findByPrestation_Pole_Id_ShouldReturnPolePhotos() {
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");
        Photo p1 = RepositoryTestHelper.persistValidPhoto(entityManager, "http://honeygroup.fr/photo2.jpg");
        
        Prestation prestation = Prestation.builder()
                .pole(pole)
                .photo(p1)
                .titreService("Safari")
                .description("Description valide de plus de 10 caractères")
                .prixBase(100.0)
                .build();
        entityManager.persist(prestation);

        List<Photo> result = photoRepository.findByPrestation_Pole_Id(pole.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(p1);
    }
}