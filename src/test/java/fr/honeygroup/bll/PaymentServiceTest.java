package fr.honeygroup.bll;

import static org.assertj.core.api.Assertions.assertThat;
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

import fr.honeygroup.bll.impl.PaymentServiceImpl;
import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.Booking;
import fr.honeygroup.enumeration.StatutPayment;
import fr.honeygroup.enumeration.StatutBooking;
import fr.honeygroup.enumeration.TypePayment;
import fr.honeygroup.repository.PaymentRepository;
import fr.honeygroup.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service PaymentService")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Paiement : Valider un paiement existant")
    void validerPaiement_ShouldUpdateStatusToValide() {
        // 1. Préparation
        Long paymentId = 10L;
        
        Booking booking = Booking.builder()
                .id(100L)
                .statut(StatutBooking.EN_ATTENTE_PAIEMENT)
                .build();

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatutPaiement(StatutPayment.EN_VERIFICATION);
        payment.setMethode(TypePayment.MOBILE_MONEY);
        payment.setTransactionId("TX-1234");
        payment.setPreuveUrl("http://preuve.pdf");
        payment.setBooking(booking);
        
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // 2. Exécution
        String result = paymentService.validerPaiement(paymentId);

        // 3. Vérifications
        assertThat(result).contains("Paiement validé avec succès");
        assertThat(payment.getStatutPaiement()).isEqualTo(StatutPayment.VALIDE);
        verify(paymentRepository, times(1)).save(payment);
        verify(bookingRepository, times(1)).save(booking);
    }
}