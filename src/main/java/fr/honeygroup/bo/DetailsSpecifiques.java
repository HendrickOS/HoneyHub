package fr.honeygroup.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entité représentant les critères spécifiques, caractéristiques ou spécifications métiers d'un prospect.
 * <p>
 * Cette classe implémente un modèle flexible de type clé/valeur (Entity-Attribute-Value). Elle permet 
 * d'étendre dynamiquement les besoins sur-mesure d'une demande de contact (comme les options d'hébergement pour 
 * l'Écotourisme ou les besoins techniques pour l'IT) sans altérer rigidement le schéma relationnel de la base de données.
 * </p>
 */
@Entity
@Table(name = "DETAILS_SPECIFIQUES") // Normalisation en majuscules pour le SGBD MariaDB
@Getter @Setter // Utilisation isolée des Getters/Setters pour sécuriser la cohésion des proxy Hibernate en mode Lazy
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(exclude = "demandeLead") // Sécurité : Évite l'évaluation forcée du proxy et les récursions lors de la journalisation
public class DetailsSpecifiques {

    /**
     * Identifiant unique et clé primaire de la ligne de spécification.
     * Généré de manière séquentielle par auto-incrément natif côté base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detail")
    private Long id;

    /**
     * Libellé ou identifiant du critère recherché (ex: "techno_cible", "type_logement", "delai").
     * Contrainte de validation stricte interdisant la soumission de clés vides.
     */
    @NotBlank(message = "La clé du champ est obligatoire")
    @Column(name = "champ_cle", nullable = false, length = 100)
    private String champCle;

    /**
     * Valeur textuelle brute associée à la clé spécifiée (ex: "Spring Boot", "Cabane insolite", "3 mois").
     * L'annotation @Lob configure le stockage sous forme d'objet textuel lourd (LONGTEXT/TEXT) en base.
     */
    @Lob
    @Column(name = "valeur")
    private String valeur;

    /* * Note de maintenance : Le support alternatif des structures JSON lourdes complexes via NoSQL 
     * a été désactivé. Le format d'extraction clé/valeur standard à plat suffit à couvrir l'ensemble 
     * des besoins des formulaires dynamiques des pôles actuels.
     * * @Lob
     * @Column(name = "valeur_json")
     * private String valeurJson;
     */
   
    /**
     * Le dossier de prospection (DemandeLead) auquel est rattaché ce complément d'information.
     * <p>
     * Configuration en chargement différé (Lazy Loading) pour préserver les performances de la base.
     * L'annotation @JsonIgnore coupe la sérialisation bidirectionnelle lors de l'exposition directe dans les API REST.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false) // Alignement strict avec les clés étrangères du script SQL
    @JsonIgnore
    private DemandeLead demandeLead;
}