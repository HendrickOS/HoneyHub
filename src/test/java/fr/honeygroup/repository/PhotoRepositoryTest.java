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
        Photo photo = new Photo();
        photo.setUrlFichier("http://honeygroup.fr/photo.jpg");
        entityManager.persist(photo);

        Prestation prestation = new Prestation();
        prestation.setPhoto(photo);
        entityManager.persist(prestation);

        List<Photo> result = photoRepository.findByPrestation_Id(prestation.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUrlFichier()).isEqualTo("http://honeygroup.fr/photo.jpg");
    }

    @Test
    @DisplayName("Requête : Trouver toutes les photos d'un pôle")
    void findByPrestation_Pole_Id_ShouldReturnPolePhotos() {
        Pole pole = new Pole();
        entityManager.persist(pole);

        Photo p1 = new Photo();
        entityManager.persist(p1);
        
        Prestation prestation = new Prestation();
        prestation.setPole(pole);
        prestation.setPhoto(p1);
        entityManager.persist(prestation);

        List<Photo> result = photoRepository.findByPrestation_Pole_Id(pole.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(p1);
    }
}