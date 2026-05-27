package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.User;

@DataJpaTest
@DisplayName("Tests du repository PaymentRepository")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Requête : Trouver un paiement par transactionId (Anti-fraude)")
    void findByTransactionId_ShouldReturnPayment() {
        String txId = "STRIPE_12345";
        Payment payment = new Payment();
        payment.setTransactionId(txId);
        payment.setMontantPaye(new BigDecimal("100.00"));
        entityManager.persist(payment);

        Optional<Payment> result = paymentRepository.findByTransactionId(txId);

        assertThat(result).isPresent();
        assertThat(result.get().getTransactionId()).isEqualTo(txId);
    }

    @Test
    @DisplayName("Requête : Trouver tous les paiements par BookingId")
    void findByBookingId_ShouldReturnPayments() {
        Booking booking = new Booking();
        entityManager.persist(booking);

        Payment p1 = new Payment();
        p1.setBooking(booking);
        entityManager.persist(p1);

        List<Payment> results = paymentRepository.findByBookingId(booking.getId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getBooking().getId()).isEqualTo(booking.getId());
    }

    @Test
    @DisplayName("Requête : Trouver les paiements par email utilisateur")
    void findByBookingUserEmail_ShouldReturnUserPayments() {
        User user = new User();
        user.setEmail("client@honeygroup.fr");
        entityManager.persist(user);

        Booking booking = new Booking();
        booking.setUser(user);
        entityManager.persist(booking);

        Payment payment = new Payment();
        payment.setBooking(booking);
        entityManager.persist(payment);

        List<Payment> results = paymentRepository.findByBookingUserEmail("client@honeygroup.fr");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getBooking().getUser().getEmail()).isEqualTo("client@honeygroup.fr");
    }
}