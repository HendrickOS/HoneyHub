package fr.honeygroup.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import fr.honeygroup.enumeration.StatutBooking;
import fr.honeygroup.enumeration.TypeReservation;

/**
 * Entité représentant une réservation (Booking) au sein du pôle Écotourisme.
 * <p>
 * Cette classe fait le lien entre un utilisateur (le client), une session temporelle 
 * spécifique de voyage, et l'historique des flux financiers associés (les paiements).
 * </p>
 */
@Entity
@Table(name = "BOOKING")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(exclude = {"user", "session", "payments"}) // Sécurité : Exclut les relations pour éviter les récursions cycliques dans les logs
public class Booking {

    /**
     * Identifiant unique et clé primaire de la réservation.
     * Généré automatiquement par la base de données via une stratégie d'auto-incrément.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * L'utilisateur (client) ayant initié la réservation.
     * Stratégie Lazy Loading activée pour optimiser les performances de chargement.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * La session de voyage spécifique associée à cette réservation.
     * Représente le pivot de l'architecture normalisée (porte les dates fixes et la capacité).
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    /**
     * JAUGE METIER : Nombre de places contractées par le client pour cette réservation.
     * Permet de calculer dynamiquement le taux de remplissage de la session associée.
     * Valeur initialisée par défaut à 1 place (commodité d'usage).
     */
    @NotNull
    @Column(name = "nb_places", nullable = false)
    private Integer nbPlaces = 1;
    
    /**
     * Définit la nature de la réservation pour orienter le workflow métier.
     * <p>
     * Le type "SESSION" correspond au catalogue standard (tourisme), 
     * tandis que "SUR_MESURE" est réservé aux prestations IT spécifiques.
     * Cette contrainte est obligatoire pour assurer la traçabilité des dossiers.
     * </p>
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_reservation", nullable = false, length = 20)
    private TypeReservation typeReservation;

    /**
     * Date et heure de l'enregistrement de la réservation par le système.
     * Non modifiable après l'insertion initiale pour garantir l'intégrité de l'audit.
     * Gérée automatiquement via le cycle de vie JPA (@PrePersist).
     */
    @Column(name = "date_creation_resa", updatable = false, nullable = false)
    private LocalDateTime dateCreationResa;

    /**
     * Statut actuel du dossier dans le Workflow métier de Honey Group.
     * Persisté sous forme de chaîne de caractères (String) en base pour une meilleure lisibilité SQL.
     * Initialisé obligatoirement à {@link StatutBooking#EN_ATTENTE_PAIEMENT} avant validation de la preuve d'upload.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private StatutBooking statut = StatutBooking.EN_ATTENTE_PAIEMENT;

    /**
     * Enveloppe budgétaire globale de la réservation.
     * Calculé côté service : (Prix unitaire de la prestation parente * nbPlaces).
     * Type BigDecimal utilisé pour garantir une précision financière absolue et éviter les arrondis flottants.
     */
    @NotNull
    @Column(name = "montant_total", precision = 18, scale = 2)
    private BigDecimal montantTotal;

    /**
     * Liste des flux de transactions ou acomptes financiers rattachés à ce dossier.
     * La cascade complète permet de propager les sauvegardes et suppressions (orphanRemoval) de manière transparente.
     */
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    /**
     * Hook de cycle de vie JPA exécuté automatiquement avant l'insertion en base de données.
     * Garantit l'intégrité de l'audit temporel sans surcharger la couche de service technique (BLL).
     */
    @PrePersist
    protected void onCreate() {
        if (this.dateCreationResa == null) {
            this.dateCreationResa = LocalDateTime.now();
        }
    }
}