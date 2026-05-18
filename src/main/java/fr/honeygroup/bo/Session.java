package fr.honeygroup.bo;

import java.time.LocalDateTime;
import java.util.List;

import enumeration.StatutSession;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entité représentant une session temporelle (Session) rattachée à une prestation.
 * <p>
 * Cette classe constitue le pivot opérationnel du modèle de réservation du pôle Écotourisme.
 * Elle permet de planifier des créneaux de dates fermes pour les circuits de voyage,
 * et d'assurer le contrôle des jauges de remplissage en temps réel (capacités maximales).
 * </p>
 */
@Entity
@Table(name = "SESSION")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(exclude = {"prestation", "bookings"}) // Sécurité : Prévient les risques de récursion infinie ou de LazyLoading forcé dans les logs
public class Session {

    /**
     * Identifiant unique et clé primaire de la session de voyage.
     * Généré de façon séquentielle par auto-incrémentation native côté SGBD.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session")
    private Long id;

    /**
     * Date et heure de départ effectif du séjour écotouristique.
     */
    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    /**
     * Date et heure de fin ou de retour du séjour écotouristique.
     */
    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    /**
     * Seuil maximal de participants tolérés simultanément pour cette session de voyage.
     * Sert de garde-fou absolu lors de la validation d'une nouvelle réservation.
     */
    @NotNull(message = "La capacité maximale est obligatoire")
    @Column(name = "capacite_max", nullable = false)
    private Integer capaciteMax;

    /**
     * Compteur dynamique représentant le nombre cumulé de places actuellement réservées et validées.
     * Initialisé à zéro lors de la création de la session par le gestionnaire.
     */
    @Builder.Default
    @Column(name = "nb_inscrits", nullable = false)
    private Integer nbInscrits = 0;

    /**
     * État opérationnel et cycle de vie de la session (OUVERT, COMPLET, EN_COURS, CLOTURE, ANNULE).
     * Mappé sous forme de chaîne de caractères (String) pour une lisibilité optimale en base de données.
     */
    @NotNull(message = "Le statut de la session est obligatoire")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "statut_session", nullable = false)
    private StatutSession statutSession = StatutSession.OUVERT;

    /**
     * La prestation catalogue de type écotourisme associée à ce calendrier de départs.
     * Configuré explicitement en chargement différé (Lazy Loading) pour optimiser les performances.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestation_id", nullable = false)
    private Prestation prestation;

    /**
     * Relation inverse listant l'ensemble des dossiers de réservations enregistrés sur ce créneau spécifique.
     * Les opérations d'écriture sur la session se propagent en cascade sur les lignes de réservation associées.
     */
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
}