package fr.honeygroup.bo.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponse {
    private Long id;
    
    // Informations Client
    private Long userId;
    private String userNomComplet; // Fusion Nom + Prénom pour l'affichage
    
    // Informations Catalogue
    private Long poleId;
    private String poleNom;
    private Long prestationId;
    private String prestationTitre;
    
    // Détails Réservation
    private LocalDateTime dateResa;
    private String statut;
    private BigDecimal montantTotal;
    
    // Détails Financiers (Optionnel : liste des paiements liés)
    private List<PaymentResponse> payments;
}