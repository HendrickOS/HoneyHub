package fr.honeygroup.bo.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Objet de transfert de données (DTO Response) modélisant la réponse structurée renvoyée
 * par l'API après le traitement ou la consultation d'un dossier de prospection (Lead).
 * <p>
 * Ce DTO sécurise l'exposition des opportunités commerciales. Il transforme la collection 
 * relationnelle de spécifications en une structure clé/valeur (Map) épurée, facilitant l'affichage 
 * des besoins sur-mesure (pôle IT ou Écotourisme) côté Frontend.
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
     * Identifiant technique unique de l'utilisateur ou prospect émetteur.
     */
    private Long userId;

    /**
     * Identité civile agrégée de l'émetteur, formatée en "NOM Prénom" par le composant de mapping 
     * pour simplifier l'exploitation sur le tableau de bord des gestionnaires.
     */
    private String userNomComplet;

    /**
     * Identifiant technique unique du pôle d'activité concerné (Écotourisme ou IT Outsourcing).
     */
    private Long poleId;

    /**
     * Identifiant unique de la prestation catalogue ciblée (peut être nul si expression de besoin purement sur-mesure).
     */
    private Long prestationId;

    /**
     * Libellé commercial de la prestation catalogue ciblée.
     */
    private String prestationTitre;

    /**
     * Cartographie dictionnaire des besoins ou critères spécifiques extraits de la base de données.
     * Restitue les informations sous forme de paires Clé/Valeur fluides pour l'intégration.
     */
    private Map<String, String> specificDetails;
}