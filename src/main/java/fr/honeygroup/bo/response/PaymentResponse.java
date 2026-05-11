package fr.honeygroup.bo.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private Integer id;
    private String methode;      // ex: STRIPE, VIREMENT
    private String transactionId;
    private BigDecimal montantPaye;
    private LocalDateTime datePaiement;
    private String statutPaiement; // ex: VALIDE, EN_ATTENTE
    private String preuveUrl;      // Pour afficher le lien vers le PDF du virement
}