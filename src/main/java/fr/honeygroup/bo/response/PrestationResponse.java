package fr.honeygroup.bo.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de donnees (DTO) unifie representant une prestation du catalogue.
 * <p>
 * Ce payload adopte une structure aplatie regroupant a la fois les attributs generiques du catalogue 
 * et les proprietes specifiques aux sous-types (Circuits touristiques ou Cours de langues). 
 * Cette conception simplifie grandement la consommation et le rendu polymorphique des donnees 
 * par l'application cliente grace a la presence d'un indicateur de type.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrestationResponse {
    
    // ==========================================
    // Champs Communs de l'Offre Catalogue
    // ==========================================

    /**
     * Identifiant technique de la prestation en base de donnees.
     */
    private Long id;

    /**
     * Identifiant unique du pole d'activite organisationnel de rattachement.
     */
    private Long poleId;

    /**
     * Indicateur discriminant identifiant la nature exacte de l'offre.
     * <p>
     * Valeurs possibles admissibles : "CIRCUIT", "COURS_LANGUE" ou "GENERIQUE".
     * </p>
     */
    private String type;

    /**
     * Titre ou libelle commercial de la prestation affiche dans le catalogue.
     */
    private String titreService;

    /**
     * Descriptif textuel synthetique ou resume de l'offre.
     */
    private String description;
    
    /**
     * Tarification ou prix de base hors options applique a la prestation.
     */
    private Double prixBase;

    /**
     * Statut operationnel actuel de l'offre (ex: "ACTIF", "ARCHIVE", "BROUILLON").
     */
    private String statut;

    /**
     * Horodatage precis de la creation de la prestation pour le suivi du catalogue.
     */
    private LocalDateTime dateCreation;
    
    // ==========================================
    // Champs Specifiques aux Offres de "CIRCUIT"
    // ==========================================

    /**
     * Presentation longue, detaillee et immersive de l'excursion ou du voyage.
     * <p>
     * Ce champ n'est alimente que si le type de la prestation correspond a un "CIRCUIT".
     * </p>
     */
    private String descriptionLongue;

    /**
     * Details textuels decrivant les etapes, escales et points d'interet du parcours.
     * <p>
     * Ce champ n'est alimente que si le type de la prestation correspond a un "CIRCUIT".
     * </p>
     */
    private String itineraire;

    /**
     * Duree globale formalisee du sejour (ex: "7 jours / 6 nuits").
     * <p>
     * Ce champ n'est alimente que si le type de la prestation correspond a un "CIRCUIT".
     * </p>
     */
    private String duree;
    
    // ==========================================
    // Champs Specifiques aux "COURS_LANGUE"
    // ==========================================

    /**
     * Langue etudiee ou enseignee dans le cadre de la formation linguistique.
     * <p>
     * Ce champ n'est alimente que si le type de la prestation correspond a un "COURS_LANGUE".
     * </p>
     */
    private String langue;

    /**
     * Niveau de competence cible ou requis pour la session (ex: "B1", "Debutant").
     * <p>
     * Ce champ n'est alimente que si le type de la prestation correspond a un "COURS_LANGUE".
     * </p>
     */
    private String niveau;

    /**
     * Plan pedagogique complet et contenu detaile du programme de cours.
     * <p>
     * Ce champ n'est alimente que si le type de la prestation correspond a un "COURS_LANGUE".
     * </p>
     */
    private String descriptifProgramme;
}