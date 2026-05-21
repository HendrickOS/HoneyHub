package fr.honeygroup.bo.response;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * Objet de transfert de données (DTO Response) modélisant la réponse structurée renvoyée
 * par l'API après le traitement ou la consultation d'un dossier de prospection (Lead).
 * <p>
 * Ce DTO sécurise l'exposition des opportunités commerciales. Il transforme la collection 
 * relationnelle de spécifications en une structure clé/valeur (Map) épurée, facilitant l'affichage 
 * des besoins sur-mesure (pôle IT ou Écotourisme) côté Frontend. Il supporte de manière transparente 
 * la restitution des profils clients enregistrés ou des simples visiteurs anonymes.
 * </p>
 */
@Data
@Builder
public class LeadResponse {

    /**
     * Identifiant technique unique de la demande de lead.
     */
    private Long id;

    /**
     * Horodatage système de la soumission initiale du formulaire par le prospect.
     */
    private LocalDateTime dateSoumission;

    /**
     * Libellé de l'état d'avancement du lead au sein du tunnel commercial (ex: NOUVEAU, EN_COURS, CONVERTI).
     */
    private String statut;

    /**
     * Canal d'acquisition ou origine déclarée du contact (ex: WEB, Instagram, Google).
     */
    private String source;

    /**
     * Identifiant technique unique de l'utilisateur émetteur (nul si la demande provient d'un visiteur anonyme).
     */
    private Long userId;

    /**
     * Identité civile agrégée de l'émetteur enregistré, formatée en "NOM Prénom" par le composant de mapping 
     * pour simplifier l'exploitation sur le tableau de bord des gestionnaires (nul si parcours visiteur).
     */
    private String userNomComplet;

    /**
     * Identifiant technique unique du pôle d'activité concerné (Écotourisme ou IT Outsourcing).
     */
    private Long poleId;
    
    /**
     * Nom renseigné par le prospect (utilisé principalement pour qualifier un visiteur non authentifié).
     */
    private String nomContact;
    
    /**
     * Adresse électronique de messagerie déclarée pour recontacter le prospect.
     */
    private String emailContact;

    /**
     * Libellé ou dénomination commerciale du pôle d'activité concerné (ex: "Écotourisme").
     * Évite au client Frontend d'effectuer un appel d'API supplémentaire de résolution d'identité.
     */
    private String poleNom;

    /* * Note de maintenance : Les références directes au catalogue de prestations standardisées 
     * ont été retirées. Les détails de l'offre ou de la réservation sont désormais entièrement déportés 
     * dans le dictionnaire dynamique specificDetails.
     * * private Long prestationId;
     * * private String prestationTitre;
     */

    /**
     * Cartographie dictionnaire des besoins ou critères spécifiques extraits de la base de données.
     * Restitue les informations sous forme de paires Clé/Valeur fluides pour l'intégration.
     */
    private Map<String, String> specificDetails;
}