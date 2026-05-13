package fr.honeygroup.bo.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrestationResponse {
    
    // Champs communs
    private Long id;
    private Long poleId;
    private String type; // "CIRCUIT", "COURS_LANGUE", "GENERIQUE"
    private String titreService;
    private String description;
    private Double prixBase;
    private String statut;
    private LocalDateTime dateCreation;
    
    // Champs Circuit
    private String descriptionLongue;
    private String itineraire;
    private String duree;
    
    // Champs CoursLangue
    private String langue;
    private String niveau;
    private String descriptifProgramme;
}
