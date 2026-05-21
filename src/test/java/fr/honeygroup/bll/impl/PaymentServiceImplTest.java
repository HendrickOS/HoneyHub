package fr.honeygroup.bll.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.response.PaymentResponse;
import fr.honeygroup.enumeration.StatutBooking;
import fr.honeygroup.enumeration.StatutPayment;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessLogicException;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException;
import fr.honeygroup.mapper.PaymentMapper;
import fr.honeygroup.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

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
    @DisplayName("Validation paiement - Succès avec mise à jour booking")
    void validerPaiement_ShouldUpdateStatuts_WhenTransitionValid() {
        Booking booking = new Booking();
        Payment payment = Payment.builder()
                .id(1L)
                .statutPaiement(StatutPayment.EN_VERIFICATION)
                .booking(booking)
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        paymentService.validerPaiement(1L);

        assertEquals(StatutPayment.VALIDE, payment.getStatutPaiement());
        assertEquals(StatutBooking.CONFIRME, booking.getStatut());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    @DisplayName("Validation paiement - Exception si transition illégale")
    void validerPaiement_ShouldThrowSecurityException_WhenTransitionIllegal() {
        Payment payment = Payment.builder()
                .id(1L)
                .statutPaiement(StatutPayment.REJETE) // Transition REJETE -> VALIDE est impossible
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThrows(BusinessSecurityException.class, () -> paymentService.validerPaiement(1L));
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