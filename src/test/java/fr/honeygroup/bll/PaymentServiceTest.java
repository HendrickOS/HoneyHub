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
import fr.honeygroup.enumeration.StatutPayment;
import fr.honeygroup.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service PaymentService")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Paiement : Valider un paiement existant")
    void validerPaiement_ShouldUpdateStatusToValide() {
        // 1. Préparation
        Long paymentId = 10L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatutPaiement(StatutPayment.EN_ATTENTE_PREUVE);
        
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // 2. Exécution
        String result = paymentService.validerPaiement(paymentId);

        // 3. Vérifications
        assertThat(result).isEqualTo("SUCCES");
        assertThat(payment.getStatutPaiement()).isEqualTo(StatutPayment.VALIDE);
        verify(paymentRepository, times(1)).save(payment);
    }
}