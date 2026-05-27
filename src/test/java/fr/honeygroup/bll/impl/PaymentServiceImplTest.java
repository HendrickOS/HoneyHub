package fr.honeygroup.bll.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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

import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.request.PaymentRequest;
import fr.honeygroup.bo.response.PaymentResponse;
import fr.honeygroup.enumeration.StatutBooking;
import fr.honeygroup.enumeration.StatutPayment;
import fr.honeygroup.enumeration.TypePayment;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessLogicException;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException;
import fr.honeygroup.mapper.PaymentMapper;
import fr.honeygroup.repository.BookingRepository;
import fr.honeygroup.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests des règles métier de PaymentService (BLL)")
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private BookingRepository bookingRepository; // Ajouté : nécessaire pour validerPaiement
    @Mock private PaymentMapper paymentMapper;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Booking bookingValide;

    @BeforeEach
    void setUp() {
        bookingValide = new Booking();
        bookingValide.setId(10L);
        bookingValide.setStatut(StatutBooking.EN_ATTENTE_PAIEMENT);
    }

    @Test
    @DisplayName("Récupération détail paiement - Succès")
    void getPaymentDetails_ShouldReturnResponse_WhenFound() {
        Payment payment = Payment.builder().id(1L).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(new PaymentResponse());

        assertNotNull(paymentService.getPaymentDetails(1L));
    }

    @Test
    @DisplayName("Récupération détail paiement - Exception si introuvable")
    void getPaymentDetails_ShouldThrowBusinessLogicException_WhenNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(BusinessLogicException.class, () -> paymentService.getPaymentDetails(99L));
    }

    @Test
    @DisplayName("Historique des paiements par booking")
    void getPaymentsByBooking_ShouldReturnList() {
        Payment p = Payment.builder().id(1L).build();
        when(paymentRepository.findByBookingId(10L)).thenReturn(List.of(p));
        when(paymentMapper.toResponse(p)).thenReturn(new PaymentResponse());

        List<PaymentResponse> results = paymentService.getPaymentsByBooking(10L);
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Historique des paiements de l'utilisateur connecté (Dashboard)")
    void getPaymentsForCurrentUser_ShouldReturnList() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("client@honeygroup.fr");
        
        Payment p = Payment.builder().id(1L).build();
        when(paymentRepository.findByBookingUserEmail("client@honeygroup.fr")).thenReturn(List.of(p));
        when(paymentMapper.toResponse(p)).thenReturn(new PaymentResponse());

        List<PaymentResponse> results = paymentService.getPaymentsForCurrentUser();
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Historique des paiements liés à une session (Staff)")
    void getPaymentsBySession_ShouldReturnList() {
        Payment p = Payment.builder().id(1L).build();
        when(paymentRepository.findByBookingSessionId(5L)).thenReturn(List.of(p));
        when(paymentMapper.toResponse(p)).thenReturn(new PaymentResponse());

        List<PaymentResponse> results = paymentService.getPaymentsBySession(5L);
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Historique des paiements d'un client ciblé")
    void getPaymentsByUser_ShouldReturnList() {
        Payment p = Payment.builder().id(1L).build();
        when(paymentRepository.findByBookingUserId(100L)).thenReturn(List.of(p));
        when(paymentMapper.toResponse(p)).thenReturn(new PaymentResponse());

        List<PaymentResponse> results = paymentService.getPaymentsByUser(100L);
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Confirmation paiement - Succès du dépôt de preuve client")
    void confirmerPaiement_ShouldUpdateDataAndStatut() {
        Payment payment = Payment.builder()
                .id(1L)
                .statutPaiement(StatutPayment.EN_ATTENTE_PREUVE)
                .build();
        
        PaymentRequest request = new PaymentRequest();
        request.setMethode(TypePayment.VIREMENT_BANCAIRE);
        request.setTransactionId("TX123");
        request.setPreuveUrl("http://preuve.fr");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        paymentService.confirmerPaiement(1L, request);

        assertEquals(StatutPayment.EN_VERIFICATION, payment.getStatutPaiement());
        assertEquals(TypePayment.VIREMENT_BANCAIRE, payment.getMethode());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    @DisplayName("Validation paiement - Succès avec mise à jour booking")
    void validerPaiement_ShouldUpdateStatuts_WhenTransitionValid() {
        // Ajout des données obligatoires pour passer la validation de ton étape 2
        Payment payment = Payment.builder()
                .id(1L)
                .statutPaiement(StatutPayment.EN_VERIFICATION)
                .methode(TypePayment.VIREMENT_BANCAIRE)
                .transactionId("TX_123456")
                .preuveUrl("http://honeygroup.fr/preuve.pdf")
                .booking(bookingValide)
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        paymentService.validerPaiement(1L);

        assertEquals(StatutPayment.VALIDE, payment.getStatutPaiement());
        assertEquals(StatutBooking.CONFIRME, bookingValide.getStatut());
        verify(paymentRepository, times(1)).save(payment);
        verify(bookingRepository, times(1)).save(bookingValide); // Vérification de la persistance du dossier
    }

    @Test
    @DisplayName("Validation paiement - Exception si pièces justificatives manquantes")
    void validerPaiement_ShouldThrowSecurityException_WhenDataIncomplete() {
        Payment paymentIncomplet = Payment.builder()
                .id(1L)
                .statutPaiement(StatutPayment.EN_VERIFICATION)
                .booking(bookingValide)
                .methode(null) // Donnée manquante
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentIncomplet));

        assertThrows(BusinessSecurityException.class, () -> paymentService.validerPaiement(1L));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Validation paiement - Exception si le dossier de réservation a été annulé")
    void validerPaiement_ShouldThrowSecurityException_WhenBookingAnnule() {
        bookingValide.setStatut(StatutBooking.ANNULE); // Dossier annulé
        Payment payment = Payment.builder()
                .id(1L)
                .statutPaiement(StatutPayment.EN_VERIFICATION)
                .methode(TypePayment.VIREMENT_BANCAIRE)
                .transactionId("TX123")
                .preuveUrl("http://url")
                .booking(bookingValide)
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThrows(BusinessSecurityException.class, () -> paymentService.validerPaiement(1L));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Validation paiement - Exception si transition illégale")
    void validerPaiement_ShouldThrowSecurityException_WhenTransitionIllegal() {
        Payment payment = Payment.builder()
                .id(1L)
                .statutPaiement(StatutPayment.REJETE) // Transition REJETE -> VALIDE impossible
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThrows(IllegalStateException.class, () -> paymentService.validerPaiement(1L));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rejet paiement - Succès")
    void rejeterPaiement_ShouldUpdateStatut_WhenTransitionValid() {
        Payment payment = Payment.builder()
                .id(1L)
                .statutPaiement(StatutPayment.EN_VERIFICATION)
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        paymentService.rejeterPaiement(1L);

        assertEquals(StatutPayment.REJETE, payment.getStatutPaiement());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    @DisplayName("Rejet paiement - Exception si transition illégale")
    void rejeterPaiement_ShouldThrowSecurityException_WhenTransitionIllegal() {
        Payment payment = Payment.builder()
                .id(1L)
                .statutPaiement(StatutPayment.VALIDE) // Impossible de rejeter un paiement déjà validé
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThrows(BusinessSecurityException.class, () -> paymentService.rejeterPaiement(1L));
    }
}