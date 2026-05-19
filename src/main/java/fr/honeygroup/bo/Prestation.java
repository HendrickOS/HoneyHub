package fr.honeygroup.bo;

import java.time.LocalDateTime;
import java.util.List;

import enumeration.StatutPrestation;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entité maîtresse représentant le catalogue général des prestations de Honey Group.
 * <p>
 * Utilise la stratégie d'héritage {@link InheritanceType#JOINED} permettant de mapper 
 * proprement les sous-classes spécifiques (ex: Circuit, CoursLangue) dans des tables dédiées,
 * tout en centralisant les attributs communs dans la table pivot PRESTATION.
 * </p>
 */
@Entity
@Table(name = "PRESTATION")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"pole", "photo", "sessions"}) // Sécurité : Évite les résolutions cycliques lors de l'appel de toString() sur les relations
public class Prestation {

    /**
     * Identifiant technique unique de la prestation.
     * Mappé sur la clé primaire auto-incrémentée du catalogue.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestation")
    private Long id;

    /**
     * Le pôle d'activité (Écotourisme, IT Outsourcing...) rattaché à l'offre.
     * Liaison obligatoire (optional = false) pour assurer le cloisonnement fonctionnel du catalogue.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pole", nullable = false)
    private Pole pole;

    /**
     * Illustration visuelle associée à la fiche descriptive du service.
     * Relation facultative chargée à la demande (Lazy Loading).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_photo")
    private Photo photo;

    /**
     * Intitulé commercial de la prestation visible par les utilisateurs sur le catalogue.
     */
    @NotBlank
    @Size(min = 3, max = 255)
    @Column(name = "titre_service", nullable = false)
    private String titreService;

    /**
     * Description détaillée présentant le contenu, le programme ou les spécificités de l'offre.
     * Longueur étendue à 2000 caractères pour stocker des textes riches.
     */
    @NotBlank
    @Size(min = 10, max = 2000)
    @Column(nullable = false, length = 2000)
    private String description;
    
    /**
     * Prix de référence unitaire de la prestation hors ajustements de session.
     */
    @NotNull
    @Column(name = "prix_base", nullable = false)
    private Double prixBase;

    /**
     * État de visibilité de l'offre (ex: ACTIF, ARCHIVE).
     * Géré par l'énumération {@link StatutPrestation} sous forme textuelle en base.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPrestation statut = StatutPrestation.ACTIF;

    /**
     * Horodatage de l'insertion de l'offre en base de données.
     * Propriété figée à la création (updatable = false).
     */
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    /**
     * Agrégation bidirectionnelle recensant l'historique et les programmations de sessions futures.
     * Spécifique au pôle Écotourisme pour lister dynamiquement les dates d'ouvertures d'un voyage.
     */
    @OneToMany(mappedBy = "prestation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Session> sessions;

    /**
     * Intercepteur de cycle de vie JPA injectant la date système au moment de la persistance initiale.
     */
    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}