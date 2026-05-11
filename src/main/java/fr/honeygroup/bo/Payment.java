package fr.honeygroup.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @NotBlank
    @Column(name = "methode", length = 50, nullable = false)
    private String methode; // ex: STRIPE, PAYPAL, VIREMENT

    @Column(name = "transaction_id", unique = true, length = 100)
    private String transactionId;

    @NotNull
    @Column(name = "montant_paye", precision = 18, scale = 2, nullable = false)
    private BigDecimal montantPaye;

    @Column(name = "preuve_url", length = 500)
    private String preuveUrl; // Utile pour les preuves de virement (PDF/Image)

    @Column(name = "date_paiement", updatable = false)
    private LocalDateTime datePaiement = LocalDateTime.now();

    @Column(name = "statut_paiement", length = 50)
    private String statutPaiement = "EN VERIFICATION";
}