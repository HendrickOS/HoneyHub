package fr.honeygroup.bll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.honeygroup.bll.impl.BookingServiceImpl;
import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;
import fr.honeygroup.repository.BookingRepository;
import fr.honeygroup.repository.SessionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service BookingService")
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("Réservation : Création avec succès (Scénario Nominal)")
    void creerReservationSandbox_ShouldCreateBooking_WhenSessionValid() {
        // 1. Préparation
        BookingRequest request = new BookingRequest();
        request.setSessionId(1L);
        
        Session session = new Session();
        session.setId(1L);
        session.setCapaciteMax(10);
        session.setNbInscrits(5);
        
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. Exécution
        BookingResponse response = bookingService.creerReservationSandbox(request);

        // 3. Vérifications
        assertThat(response).isNotNull();
        verify(bookingRepository, times(1)).save(any(Booking.class));
        // Vérification que le nombre d'inscrits a été incrémenté logiquement dans le service
        assertThat(session.getNbInscrits()).isEqualTo(6);
    }
}