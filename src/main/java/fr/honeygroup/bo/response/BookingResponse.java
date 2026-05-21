package fr.honeygroup.bo.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import enumeration.TypeReservation;
import lombok.Builder;
import lombok.Data;

/**
 * Objet de transfert de données (DTO Response) modélisant la réponse structurée renvoyée
 * par l'API après le traitement, la création ou la consultation d'une réservation (Booking).
 * <p>
 * Cette classe aplatit et sécurise l'arbre d'entités Hibernate afin de n'exposer que les 
 * propriétés indispensables aux interfaces utilisateurs, éliminant ainsi les risques de fuites 
 * de données sensibles (telles que les mots de passe) ou de boucles de sérialisation JSON.
 * </p>
 */
@Data
@Builder
public class BookingResponse {
    
    /**
     * Identifiant technique unique de la réservation.
     */
    private Long id;
    
    /**
     * Type de la réservation permettant d'identifier le workflow métier associé.
     * <p>
     * Indique s'il s'agit d'une session de catalogue standard ("SESSION") 
     * ou d'une prestation spécifique ("SUR_MESURE").
     * </p>
     */
    private TypeReservation typeReservation;
    
    // ============================================================================
    // INFORMATIONS CLIENT
    // ============================================================================
    
    /**
     * Identifiant unique du client propriétaire de la réservation.
     */
    private Long userId;

    /**
     * Identité civile agrégée de l'usager, formatée de manière conventionnelle 
     * en "NOM Prénom" par la couche de mapping pour l'affichage direct sur le front-end.
     */
    private String userNomComplet;
    
    // ============================================================================
    // INFORMATIONS CATALOGUE (Transités par l'objet Session)
    // ============================================================================
    
    /**
     * Identifiant unique du pôle d'activité associé (Écotourisme).
     */
    private Long poleId;

    /**
     * Libellé nominatif du pôle d'activité.
     */
    private String poleNom;

    /**
     * Identifiant unique de la prestation catalogue de base.
     */
    private Long prestationId;

    /**
     * Intitulé ou titre commercial de la prestation touristique.
     */
    private String prestationTitre;
    
    // ============================================================================
    // DÉTAILS DE LA SESSION DE VOYAGE (Nouveaux pivots fonctionnels)
    // ============================================================================
    
    /**
     * Identifiant technique de la session temporelle retenue pour ce voyage.
     */
    private Long sessionId;

    /**
     * Calendrier effectif : Date et heure de départ du séjour écotouristique.
     */
    private LocalDateTime dateDebutSession;

    /**
     * Calendrier effectif : Date et heure de fin ou de retour du séjour écotouristique.
     */
    private LocalDateTime dateFinSession;
    
    // ============================================================================
    // DÉTAILS RÉSERVATION
    // ============================================================================
    
    /**
     * Jauge contractée : Nombre de places réservées entrant dans le calcul du taux de remplissage de la session.
     */
    private Integer nbPersonnes;

    /**
     * Horodatage système de l'enregistrement initial de la réservation.
     */
    private LocalDateTime dateResa;

    /**
     * État d'avancement textuel de la réservation au sein du workflow métier (ex: EN_ATTENTE_PAIEMENT, CONFIRME).
     */
    private String statut;

    /**
     * Enveloppe financière globale calculée par la couche BLL (Prix unitaire * nbPersonnes).
     */
    private BigDecimal montantTotal;
    
    // ============================================================================
    // DÉTAILS FINANCIERS
    // ============================================================================
    
    /**
     * Collection des flux de versements, acomptes et liens de pièces justificatives (preuves d'upload) adossés au dossier.
     */
    private List<PaymentResponse> payments;
}