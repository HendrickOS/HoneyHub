package fr.honeygroup.bo.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest {
    
    private String nom;
    private String prenom;

    private String adresse;

    @Pattern(
    	    regexp = "^\\+?[1-9]\\d{7,14}$",
    	    message = "Numéro de téléphone invalide (format international requis)"
    	)
    private String telephone;

    private String pays;

    private String preferences;
}
