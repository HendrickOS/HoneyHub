package fr.honeygroup.bo.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class LeadResponse {
    private Long id;
    private LocalDateTime dateSoumission;
    private String statut;
    private String source;
    private Long userId;
    private String userNomComplet; // Pratique pour l'affichage
    private Long poleId;       // Ajouté car important dans ton SQL
    private Long prestationId;
    private String prestationTitre;
    private Map<String, String> specificDetails;
}