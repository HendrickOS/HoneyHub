package fr.honeygroup.repository;

import fr.honeygroup.bo.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

//    /**
//     * Récupérer toutes les photos associées à une prestation.
//     */
//    List<Photo> findByPrestationId(Integer prestationId);
//
//    /**
//     * Récupérer les photos illustrant un pôle spécifique.
//     */
//    List<Photo> findByPoleId(Integer poleId);
	
}