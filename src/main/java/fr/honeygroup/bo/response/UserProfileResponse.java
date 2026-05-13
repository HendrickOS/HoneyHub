package fr.honeygroup.bo.response;

import fr.honeygroup.bo.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private String role;
    
    // Profile info
    private String adresse;
    private String telephone;
    private String pays;
    private String preferences;
}
