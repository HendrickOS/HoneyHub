package fr.honeygroup.bll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import fr.honeygroup.bll.impl.BookingServiceImpl;
import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;
import fr.honeygroup.mapper.BookingMapper;
import fr.honeygroup.repository.BookingRepository;
import fr.honeygroup.repository.PaymentRepository;
import fr.honeygroup.repository.SessionRepository;
import fr.honeygroup.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service BookingService")
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Réservation : Création avec succès (Scénario Nominal)")
    void creerReservationSandbox_ShouldCreateBooking_WhenSessionValid() {
        // 1. Préparation
        BookingRequest request = new BookingRequest();
        request.setSessionId(1L);
        request.setUserId(100L);
        request.setNbPersonnes(1);
        
        User user = User.builder()
                .id(100L)
                .email("test@honeygroup.fr")
                .build();

        Prestation prest = new Prestation();
        prest.setPrixBase(100.0);

        Session session = new Session();
        session.setId(1L);
        session.setCapaciteMax(10);
        session.setNbInscrits(5);
        session.setPrestation(prest);

        Booking booking = new Booking();
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@honeygroup.fr");
        when(userRepository.findByEmail("test@honeygroup.fr")).thenReturn(Optional.of(user));
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));
        when(bookingMapper.toEntity(request)).thenReturn(booking);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);
        when(bookingMapper.toResponse(any(Booking.class))).thenReturn(BookingResponse.builder().build());

        // 2. Exécution
        BookingResponse response = bookingService.creerReservationSandbox(request);

        // 3. Vérifications
        assertThat(response).isNotNull();
        verify(bookingRepository, times(1)).save(any(Booking.class));
        // Vérification que le nombre d'inscrits a été incrémenté logiquement dans le service
        assertThat(session.getNbInscrits()).isEqualTo(6);
    }
}