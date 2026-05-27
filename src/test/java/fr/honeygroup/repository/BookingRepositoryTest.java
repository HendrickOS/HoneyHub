package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.User;
import fr.honeygroup.enumeration.StatutBooking;

@DataJpaTest
@DisplayName("Tests du repository BookingRepository")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Requête : Trouver par UserId ordonné par date")
    void findByUserIdOrderByDateCreationResaDesc_ShouldReturnOrderedList() {
        // 1. Préparation des données
        User user = new User();
        user.setEmail("test@honeygroup.fr");
        entityManager.persist(user);

        Booking b1 = new Booking();
        b1.setUser(user);
        b1.setDateCreationResa(LocalDateTime.now().minusDays(1));
        entityManager.persist(b1);

        Booking b2 = new Booking();
        b2.setUser(user);
        b2.setDateCreationResa(LocalDateTime.now()); // Plus récent
        entityManager.persist(b2);

        // 2. Exécution
        List<Booking> result = bookingRepository.findByUserIdOrderByDateCreationResaDesc(user.getId());

        // 3. Vérifications
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDateCreationResa()).isAfter(result.get(1).getDateCreationResa());
    }

    @Test
    @DisplayName("Requête : Trouver par SessionId")
    void findBySessionId_ShouldReturnBookings() {
        Session session = new Session();
        entityManager.persist(session);

        Booking booking = new Booking();
        booking.setSession(session);
        entityManager.persist(booking);

        List<Booking> result = bookingRepository.findBySessionId(session.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSession().getId()).isEqualTo(session.getId());
    }

    @Test
    @DisplayName("Requête : Trouver par Statut")
    void findByStatut_ShouldReturnMatchingBookings() {
        Booking b1 = new Booking();
        b1.setStatut(StatutBooking.CONFIRME);
        entityManager.persist(b1);

        List<Booking> result = bookingRepository.findByStatut("CONFIRME");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatut()).isEqualTo("CONFIRME");
    }
}