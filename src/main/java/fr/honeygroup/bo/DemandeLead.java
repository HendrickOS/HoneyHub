package fr.honeygroup.bo;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import enumeration.StatutLead;
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entité représentant un dossier de prospection ou contact commercial (DemandeLead).
 * <p>
 * Cette classe sert de point d'entrée pour la capture des leads de la plateforme. 
 * Elle est indispensable pour centraliser les expressions de besoins sur-mesure (notamment 
 * pour le pôle IT Outsourcing) afin de permettre aux gestionnaires de qualifier les demandes 
 * et d'établir des devis au cas par cas.
 * </p>
 */
@Entity
@Table(name = "DEMANDE_LEAD")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "pole", "prestation", "specificDetails"}) // Sécurité : Empêche l'évaluation des proxys et les exceptions d'affichage cycliques
public class DemandeLead {

    /**
     * Identifiant unique et clé primaire de la demande de lead.
     * Généré de manière séquentielle par auto-incrément natif côté base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande")
    private Long id;

    /**
     * Date et heure d'enregistrement initial du lead par la plateforme.
     * Définie automatiquement à la création, non modifiable pour préserver l'historique d'audit.
     */
    @Column(name = "date_soumission", nullable = false, updatable = false)
    private LocalDateTime dateSoumission;

    /**
     * État d'avancement du lead dans le tunnel de conversion commercial (ex: NOUVEAU, EN_COURS, TRAITE, REJETE).
     * Mappé au format chaîne (STRING) en base MariaDB pour simplifier le requêtage direct et les analyses décisionnelles.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_traitement", nullable = false, length = 50)
    private StatutLead statut;

    /**
     * Canal d'acquisition ou origine de l'opportunité (ex: "WEB", "CAMPAGNE_MAIL", "PARTENAIRE").
     * Valeur initialisée par défaut au format brut "WEB".
     */
    @NotBlank
    @Column(nullable = false, length = 50)
    private String source ;

    /**
     * L'utilisateur (prospect ou client enregistré) ayant soumis la demande de contact.
     * Mappé en Lazy Loading pour l'optimisation des requêtes. Le champ est ignoré lors des sérialisations JSON.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = true)
    @JsonIgnore
    private User user;
    
 // ✅ infos visiteur (si non connecté)
    private String nomContact;
    private String emailContact;

    /**
     * Le pôle d'activité concerné par cette opportunité d'affaires (Écotourisme ou IT Outsourcing).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pole", nullable = false)
    @JsonIgnore
    private Pole pole;

    /**
     * La prestation du catalogue visée par la demande (optionnelle pour l'IT Outsourcing sur-mesure).
     */
   /* @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prestation")
    @JsonIgnore
    private Prestation prestation;*/

    /**
     * Bloc de notes ou compte-rendu interne complété par les équipes de Honey Group.
     * L'annotation @Lob permet d'y stocker un historique de commentaires au format TEXT ou LONGTEXT.
     */
   /* @Lob
    @Column(name = "commentaire_interne")
    private String commentaireInterne;*/

    /**
     * Liste des caractéristiques ou critères sur-mesure rattachés à cette demande d'informations.
     * <p>
     * La cascade complète permet de propager la persistance et le nettoyage (orphanRemoval) des critères 
     * à la mise à jour ou suppression du lead parent.
     * </p>
     */
    @OneToMany(mappedBy = "demandeLead", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailsSpecifiques> specificDetails;

    /**
     * Hook du cycle de vie JPA déclenché automatiquement juste avant l'insertion (INSERT) en base de données.
     * Permet d'assurer la cohérence du modèle en initialisant par défaut la date système courante ainsi 
     * que le statut restrictif d'entrée du workflow commercial.
     */
    @PrePersist
    public void prePersist() {
        if (dateSoumission == null) dateSoumission = LocalDateTime.now();
        if (statut == null) statut = StatutLead.NOUVEAU;
    }
}