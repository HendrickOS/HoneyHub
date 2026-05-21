package fr.honeygroup.bo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.honeygroup.enumeration.StatutBooking;
import fr.honeygroup.enumeration.TypeReservation;

@DisplayName("Tests unitaires de l'entité Booking (Réservation)")
class BookingTest {

    private User userDeTest;
    private Session sessionDeTest;

    @BeforeEach
    void setUp() {
        // Initialisation des objets requis pour créer un Booking valide
        userDeTest = new User();
        sessionDeTest = new Session();
    }

    @Test
    @DisplayName("Vérification des valeurs par défaut d'un Booking à l'initialisation")
    void initialisation_DevraitAvoirLesValeursParDefaut() {
        // ARRANGE & ACT
        Booking booking = new Booking();

        // ASSERT
        assertEquals(1, booking.getNbPlaces(), "Le nombre de places par défaut doit être de 1.");
        assertEquals(StatutBooking.EN_ATTENTE_PAIEMENT, booking.getStatut(), "Le statut initial doit être EN_ATTENTE_PAIEMENT.");
        assertNull(booking.getDateCreationResa(), "La date de création doit être nulle avant le passage dans @PrePersist.");
    }

    @Test
    @DisplayName("Vérification que le Builder Lombok mappe correctement tous les champs requis")
    void builder_DevraitConstruireLObjetCorrectement() {
        // ARRANGE & ACT
        Booking booking = Booking.builder()
                .id(10L)
                .user(userDeTest)
                .session(sessionDeTest)
                .nbPlaces(4)
                .typeReservation(TypeReservation.SESSION)
                .montantTotal(new BigDecimal("399.99"))
                .statut(StatutBooking.EN_ATTENTE_PAIEMENT)
                .build();

        // ASSERT
        assertNotNull(booking);
        assertEquals(10L, booking.getId());
        assertEquals(userDeTest, booking.getUser());
        assertEquals(sessionDeTest, booking.getSession());
        assertEquals(4, booking.getNbPlaces());
        assertEquals(TypeReservation.SESSION, booking.getTypeReservation());
        assertEquals(new BigDecimal("399.99"), booking.getMontantTotal());
        assertEquals(StatutBooking.EN_ATTENTE_PAIEMENT, booking.getStatut());
    }

    @Test
    @DisplayName("Vérification que le hook @PrePersist génère une date système si elle est absente")
    void onCreate_DevraitInitialiserLaDateCreation_QuandElleEstNull() {
        // ARRANGE
        Booking booking = new Booking();
        assertNull(booking.getDateCreationResa());

        // ACT
        // On force l'appel du hook JPA @PrePersist qui se déclenche normalement avant le save
        booking.onCreate();

        // ASSERT
        assertNotNull(booking.getDateCreationResa(), "Le hook onCreate doit initialiser la date.");
        assertTrue(booking.getDateCreationResa().isBefore(LocalDateTime.now().plusSeconds(1)), "La date doit correspondre à l'instant présent.");
    }

    @Test
    @DisplayName("Vérification que le hook @PrePersist respecte et n'écrase pas une date déjà définie")
    void onCreate_NeDevraitPasEcraserLaDateCreation_SiElleExisteDeja() {
        // ARRANGE
        Booking booking = new Booking();
        LocalDateTime datePassee = LocalDateTime.now().minusDays(5);
        booking.setDateCreationResa(datePassee);

        // ACT
        booking.onCreate();

        // ASSERT
        assertEquals(datePassee, booking.getDateCreationResa(), "Le hook ne doit pas écraser une date déjà existante.");
    }
}