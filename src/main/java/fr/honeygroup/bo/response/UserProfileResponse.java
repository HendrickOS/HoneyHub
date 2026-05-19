package fr.honeygroup.bo.response;

import fr.honeygroup.bo.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de donnees (DTO) representant la reponse structuree d'un profil utilisateur complet.
 * <p>
 * Ce payload securise est utilise pour renvoyer au front-end les informations d'identite et de contact 
 * d'un utilisateur connecte. En aplatissant les donnees issues des entites User et Profile, il evite 
 * l'exposition d'informations sensibles (telles que les mots de passe haches ou les tokens internes).
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    /**
     * Identifiant technique unique de l'utilisateur en base de donnees.
     */
    private Long id;

    /**
     * Adresse email principale de l'utilisateur, servant d'identifiant unique.
     */
    private String email;

    /**
     * Nom de famille de l'utilisateur.
     */
    private String nom;

    /**
     * Prenom de l'utilisateur.
     */
    private String prenom;

    /**
     * Role ou niveau d'habilitation de l'utilisateur au sein du systeme (ex: CLIENT, ADMIN, STAFF).
     */
    private String role;
    
    /**
     * Adresse postale de residence de l'utilisateur (extraite de son profil).
     */
    private String adresse;

    /**
     * Numero de telephone de contact au format international (extrait de son profil).
     */
    private String telephone;

    /**
     * Pays de residence de l'utilisateur (extrait de son profil).
     */
    private String pays;

    /**
     * Preferences, commentaires ou besoins specifiques declares par le client (extrait de son profil).
     */
    private String preferences;
}