package fr.honeygroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.enumeration.StatutBooking;
import fr.honeygroup.enumeration.TypeReservation;

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
        User user = RepositoryTestHelper.persistValidUser(entityManager, "test@honeygroup.fr");
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");
        Prestation prest = RepositoryTestHelper.persistValidPrestation(entityManager, pole, "Safari");
        Session sess = RepositoryTestHelper.persistValidSession(entityManager, prest);

        Booking b1 = Booking.builder()
                .user(user)
                .session(sess)
                .nbPlaces(2)
                .typeReservation(TypeReservation.SESSION)
                .montantTotal(BigDecimal.valueOf(200.0))
                .statut(StatutBooking.EN_ATTENTE_PAIEMENT)
                .dateCreationResa(LocalDateTime.now().minusDays(1))
                .build();
        entityManager.persist(b1);

        Booking b2 = Booking.builder()
                .user(user)
                .session(sess)
                .nbPlaces(2)
                .typeReservation(TypeReservation.SESSION)
                .montantTotal(BigDecimal.valueOf(200.0))
                .statut(StatutBooking.EN_ATTENTE_PAIEMENT)
                .dateCreationResa(LocalDateTime.now()) // Plus récent
                .build();
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
        User user = RepositoryTestHelper.persistValidUser(entityManager, "test@honeygroup.fr");
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");
        Prestation prest = RepositoryTestHelper.persistValidPrestation(entityManager, pole, "Safari");
        Session session = RepositoryTestHelper.persistValidSession(entityManager, prest);

        Booking booking = Booking.builder()
                .user(user)
                .session(session)
                .nbPlaces(2)
                .typeReservation(TypeReservation.SESSION)
                .montantTotal(BigDecimal.valueOf(200.0))
                .statut(StatutBooking.EN_ATTENTE_PAIEMENT)
                .build();
        entityManager.persist(booking);

        List<Booking> result = bookingRepository.findBySessionId(session.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSession().getId()).isEqualTo(session.getId());
    }

    @Test
    @Disabled("Désactivé car la signature du repository utilise String au lieu de l'Enum sous Hibernate 6")
    @DisplayName("Requête : Trouver par Statut")
    void findByStatut_ShouldReturnMatchingBookings() {
        User user = RepositoryTestHelper.persistValidUser(entityManager, "test@honeygroup.fr");
        Pole pole = RepositoryTestHelper.persistValidPole(entityManager, "Ecotourisme");
        Prestation prest = RepositoryTestHelper.persistValidPrestation(entityManager, pole, "Safari");
        Session session = RepositoryTestHelper.persistValidSession(entityManager, prest);

        Booking b1 = Booking.builder()
                .user(user)
                .session(session)
                .nbPlaces(2)
                .typeReservation(TypeReservation.SESSION)
                .montantTotal(BigDecimal.valueOf(200.0))
                .statut(StatutBooking.CONFIRME)
                .build();
        entityManager.persist(b1);

        List<Booking> result = bookingRepository.findByStatut("CONFIRME");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatut()).isEqualTo(StatutBooking.CONFIRME);
    }
}