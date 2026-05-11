package fr.honeygroup.repository;

import fr.honeygroup.bo.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Pour éviter les doublons de paiement
    Optional<Payment> findByTransactionId(String transactionId);
    
    // Pour lister tous les paiements liés à une réservation précise
    List<Payment> findByBookingId(Long bookingId);
}