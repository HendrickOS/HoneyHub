package fr.honeygroup.bo;

import enumeration.StatutBooking;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "BOOKING")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(exclude = {"user", "prestation", "pole", "payments"})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestation_id", nullable = false)
    private Prestation prestation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pole_id", nullable = false)
    private Pole pole;

    @Column(name = "date_resa", updatable = false)
    private LocalDateTime dateResa = LocalDateTime.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private StatutBooking statut = StatutBooking.EN_ATTENTE;

    @NotNull
    @Column(name = "montant_total", precision = 18, scale = 2)
    private BigDecimal montantTotal;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;
}