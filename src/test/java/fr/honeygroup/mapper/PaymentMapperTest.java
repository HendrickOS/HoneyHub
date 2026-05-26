package fr.honeygroup.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.response.PaymentResponse;
import fr.honeygroup.enumeration.StatutPayment;
import fr.honeygroup.enumeration.TypePayment;

@DisplayName("Tests de mapping pour PaymentMapper")
class PaymentMapperTest {

    private final PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    @DisplayName("Mapping : Entité Payment vers PaymentResponse")
    void paymentToResponse_MappingValide() {
        // 1. Préparation de l'entité source
        Payment payment = new Payment();
        payment.setId(99L);
        payment.setMethode(TypePayment.MOBILE_MONEY);
        payment.setMontantPaye(new BigDecimal("500.50"));
        payment.setStatutPaiement(StatutPayment.VALIDE);
        payment.setPreuveUrl("/media/proofs/p99.pdf");
        payment.setDatePaiement(LocalDateTime.now());

        // 2. Exécution du mapping
        PaymentResponse response = mapper.toResponse(payment);

        // 3. Vérifications
        assertNotNull(response);
        assertEquals(99L, response.getId());
        assertEquals(TypePayment.MOBILE_MONEY, response.getMethode());
        assertEquals(new BigDecimal("500.50"), response.getMontantPaye());
        assertEquals(StatutPayment.VALIDE, response.getStatutPaiement());
        assertEquals("/media/proofs/p99.pdf", response.getPreuveUrl());
    }
}