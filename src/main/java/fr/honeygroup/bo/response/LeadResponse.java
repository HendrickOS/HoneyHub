package fr.honeygroup.bo.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class LeadResponse {
    private Integer id;
    private LocalDateTime dateSoumission;
    private String statut;
    private String source;
    private Integer userId;
    private String userNomComplet; // Pratique pour l'affichage
    private Integer poleId;       // Ajouté car important dans ton SQL
    private Integer prestationId;
    private String prestationTitre;
    private Map<String, String> details;
}