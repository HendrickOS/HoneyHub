package fr.honeygroup.bo;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.honeygroup.enumeration.StatutLead;
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
 * Entité représentant un dossier de prospection, d'opportunité d'affaires ou de contact commercial (Lead).
 * <p>
 * Cette classe sert de point d'entrée pour la capture des besoins clients sur l'ensemble de la plateforme. 
 * Elle unifie la collecte des expressions de besoins sur-mesure (hébergements spécifiques pour le pôle Écotourisme, 
 * infrastructures dédiées, etc.) afin de permettre aux gestionnaires et administrateurs de qualifier les demandes, 
 * de suivre le tunnel de conversion et d'établir des devis adéquats.
 * </p>
 *
 */
@Entity
@Table(name = "DEMANDE_LEAD")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "pole", "specificDetails"}) // Sécurité : Empêche l'évaluation prématurée des proxys Hibernate et les boucles de sérialisation cycliques
public class DemandeLead {

    /**
     * Identifiant unique et clé primaire de la demande de lead.
     * Généré de manière séquentielle par auto-incrément natif côté base de données (MariaDB/MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande")
    private Long id;

    /**
     * Date et heure d'enregistrement initial du lead par la plateforme.
     * Définie automatiquement lors de la phase de pré-persistance, non modifiable pour garantir l'historique d'audit.
     */
    @Column(name = "date_soumission", nullable = false, updatable = false)
    private LocalDateTime dateSoumission;

    /**
     * État d'avancement du lead dans le tunnel de conversion commercial (ex: NOUVEAU, EN_COURS, TRAITE, REJETE).
     * Mappé au format chaîne de caractères (STRING) en base de données pour assurer la lisibilité du schéma et la flexibilité des requêtes.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_traitement", nullable = false, length = 50)
    private StatutLead statut;

    /**
     * Canal d'acquisition ou origine géographique/technique de l'opportunité (ex: "WEB", "CAMPAGNE_MAIL").
     * Contrainte d'intégrité de surface imposant une valeur non vide (par défaut initialisée à "WEB").
     */
    @NotBlank
    @Column(nullable = false, length = 50)
    private String source;

    /**
     * L'utilisateur authentifié (client ou prospect enregistré) ayant soumis la demande.
     * Associé via un chargement paresseux (Lazy Loading). Ce champ est marqué optionnel (nullable) 
     * pour autoriser la soumission par des visiteurs anonymes.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = true)
    @JsonIgnore
    private User user;
    
    /**
     * Nom ou raison sociale renseigné par le contact. 
     * Champ obligatoire uniquement dans le cadre d'un parcours de soumission anonyme (visiteur non connecté).
     */
    @Column(name = "nom_contact", length = 100)
    private String nomContact;

    /**
     * Adresse de messagerie électronique pour recontacter le prospect. 
     * Champ obligatoire uniquement dans le cadre d'un parcours de soumission anonyme (visiteur non connecté).
     */
    @Column(name = "email_contact", length = 150)
    private String emailContact;

    /**
     * Le pôle d'activité d'Honey Group concerné et ciblé par cette opportunité d'affaires (ex: Écotourisme).
     * Association obligatoire servant d'axe de routage pour les notifications et la visibilité des équipes métiers.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pole", nullable = false)
    @JsonIgnore
    private Pole pole;

    /* * Note d'architecture [v1.1] : La relation directe avec l'entité Prestation a été désactivée. 
     * Les critères de prestations sont désormais encapsulés dynamiquement sous forme de paires clé/valeur 
     * au sein de la collection specificDetails pour offrir une flexibilité totale par pôle d'activité.
     * * @ManyToOne(fetch = FetchType.LAZY)
     * @JoinColumn(name = "id_prestation")
     * @JsonIgnore
     * private Prestation prestation;
     */

    /* * Note de maintenance [v1.1] : Champ désactivé temporairement. Les annotations de suivi textuel 
     * interne sont déportées vers le module de ticketing CRM pour éviter la surcharge de l'entité transactionnelle.
     * * @Lob
     * @Column(name = "commentaire_interne")
     * private String commentaireInterne;
     */

    /**
     * Liste des caractéristiques métiers et critères sur-mesure rattachés à cette opportunité.
     * <p>
     * La configuration applique une propagation totale en cascade (CascadeType.ALL) ainsi qu'un nettoyage des orphelins 
     * (orphanRemoval = true) pour automatiser la persistance ou la purge des lignes filles dès que la collection subit une mutation.
     * </p>
     */
    @OneToMany(mappedBy = "demandeLead", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailsSpecifiques> specificDetails;

    /**
     * Hook d'interception du cycle de vie de l'entité JPA déclenché de manière synchrone juste avant l'action d'insertion (INSERT).
     * Garantit la cohérence structurelle de l'enregistrement en forçant l'application de l'horodatage système 
     * ainsi que l'affectation du statut par défaut {@link StatutLead#NOUVEAU}.
     */
    @PrePersist
    public void prePersist() {
        if (dateSoumission == null) dateSoumission = LocalDateTime.now();
        if (statut == null) statut = StatutLead.NOUVEAU;
    }
}