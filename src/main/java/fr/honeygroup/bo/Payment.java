package fr.honeygroup.bo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fr.honeygroup.enumeration.StatutPayment;
import fr.honeygroup.enumeration.TypePayment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entité représentant un flux financier ou un règlement (Payment) adossé à un dossier.
 * <p>
 * Cette classe enregistre les transactions financières émises par les clients pour valider
 * leurs réservations écotouristiques. Elle supporte le stockage de pièces justificatives (uploads)
 * permettant un contrôle et une validation humaine asynchrone par les gestionnaires.
 * </p>
 */
@Entity
@Table(name = "PAYMENT")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(exclude = "booking") // Sécurité : Évite les boucles de récursion cyclique avec l'entité Booking lors de la journalisation
public class Payment {

    /**
     * Identifiant unique et clé primaire de la transaction financière.
     * Généré automatiquement via le mécanisme d'auto-incrément natif du SGBD.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Le dossier de réservation (Booking) associé à ce versement.
     * Configuration en chargement différé (Lazy Loading) pour optimiser l'usage de la mémoire transactionnelle.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    /**
     * Canal ou moyen de paiement sélectionné par l'usager pour régler sa réservation.
     * <p>
     * Ce champ est initialisé à {@code null} lors de la création automatique du paiement,
     * puis renseigné par l'utilisateur lors de l'envoi de sa preuve de transaction.
     * </p>
     * @see fr.honeygroup.enumeration.TypePayment
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "methode", length = 50)
    private TypePayment methode;

    /**
     * Référence ou identifiant unique de transaction émis par l'établissement bancaire ou l'opérateur télécom.
     * Fait l'objet d'une contrainte d'unicité stricte au niveau de la base de données.
     */
    @Column(name = "transaction_id", unique = true, length = 100)
    private String transactionId;

    /**
     * Valeur pécuniaire exacte perçue lors de la transaction.
     * Type BigDecimal requis pour interdire les approximations de calculs sur les types flottants primitifs.
     */
    @NotNull
    @Column(name = "montant_paye", precision = 18, scale = 2, nullable = false)
    private BigDecimal montantPaye;

    /**
     * Chemin ou URL d'accès vers la pièce justificative téléversée par le client (capture d'écran, reçu PDF).
     * Attribut indispensable au gérant pour auditer et valider le paiement de manière asynchrone.
     */
    @Column(name = "preuve_url", length = 500)
    private String preuveUrl;

    /**
     * Date et heure de l'enregistrement de l'opération financière.
     * Propriété verrouillée contre les modifications ultérieures pour préserver la traçabilité de l'audit.
     */
    @Column(name = "date_paiement", updatable = false)
    private LocalDateTime datePaiement = LocalDateTime.now();

    /**
     * État d'avancement de la vérification comptable du paiement.
     * Persisté sous forme de chaîne de caractères (String) en base de données pour une meilleure lisibilité SQL.
     * Initialisé par défaut au statut de contrôle restrictif {@link StatutPayment#EN_VERIFICATION}.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", nullable = false, length = 50)
    @Builder.Default
    private StatutPayment statutPaiement = StatutPayment.EN_VERIFICATION;
}