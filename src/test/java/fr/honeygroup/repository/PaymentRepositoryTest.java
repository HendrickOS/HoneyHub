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
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.Session;

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
        User user = RepositoryTestHelper.persistValidUser(entityManager, "test1@honeygroup.fr");
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");
        Prestation prest = RepositoryTestHelper.persistValidPrestation(entityManager, pole, "Safari");
        Session session = RepositoryTestHelper.persistValidSession(entityManager, prest);
        Booking booking = RepositoryTestHelper.persistValidBooking(entityManager, user, session);

        Payment payment = Payment.builder()
                .booking(booking)
                .transactionId(txId)
                .montantPaye(new BigDecimal("100.00"))
                .build();
        entityManager.persist(payment);

        Optional<Payment> result = paymentRepository.findByTransactionId(txId);

        assertThat(result).isPresent();
        assertThat(result.get().getTransactionId()).isEqualTo(txId);
    }

    @Test
    @DisplayName("Requête : Trouver tous les paiements par BookingId")
    void findByBookingId_ShouldReturnPayments() {
        User user = RepositoryTestHelper.persistValidUser(entityManager, "test2@honeygroup.fr");
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");
        Prestation prest = RepositoryTestHelper.persistValidPrestation(entityManager, pole, "Safari");
        Session session = RepositoryTestHelper.persistValidSession(entityManager, prest);
        Booking booking = RepositoryTestHelper.persistValidBooking(entityManager, user, session);

        Payment p1 = Payment.builder()
                .booking(booking)
                .montantPaye(new BigDecimal("50.00"))
                .build();
        entityManager.persist(p1);

        List<Payment> results = paymentRepository.findByBookingId(booking.getId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getBooking().getId()).isEqualTo(booking.getId());
    }

    @Test
    @DisplayName("Requête : Trouver les paiements par email utilisateur")
    void findByBookingUserEmail_ShouldReturnUserPayments() {
        User user = RepositoryTestHelper.persistValidUser(entityManager, "client@honeygroup.fr");
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");
        Prestation prest = RepositoryTestHelper.persistValidPrestation(entityManager, pole, "Safari");
        Session session = RepositoryTestHelper.persistValidSession(entityManager, prest);
        Booking booking = RepositoryTestHelper.persistValidBooking(entityManager, user, session);

        Payment payment = Payment.builder()
                .booking(booking)
                .montantPaye(new BigDecimal("120.00"))
                .build();
        entityManager.persist(payment);

        List<Payment> results = paymentRepository.findByBookingUserEmail("client@honeygroup.fr");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getBooking().getUser().getEmail()).isEqualTo("client@honeygroup.fr");
    }
}