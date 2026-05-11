package fr.honeygroup.repository;

import fr.honeygroup.bo.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    
    // Pour l'historique d'un client
    List<Booking> findByUserId(Integer userId);
    
    // Pour ton futur tableau de bord Admin (ex: voir toutes les réservations "EN_ATTENTE")
    List<Booking> findByStatut(String statut);
}