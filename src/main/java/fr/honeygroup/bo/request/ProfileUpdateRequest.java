package fr.honeygroup.bo.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de donnees (DTO) encapsulant la requete de mise a jour d'un profil utilisateur.
 * <p>
 * Ce payload est concu pour supporter des modifications partielles et non destructives. 
 * L'ensemble des attributs est optionnel au niveau de la saisie de surface (champs nullables), 
 * permettant a la couche BLL d'appliquer des verifications et des mutations conditionnelles.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest {
    
    /**
     * Le nouveau nom de famille a appliquer si l'utilisateur souhaite le modifier.
     * <p>
     * Champ optionnel. Si transmis, sa conformite sera verifiee par le service avant persistance.
     * </p>
     */
    private String nom;

    /**
     * Le nouveau prenom a appliquer si l'utilisateur souhaite le modifier.
     * <p>
     * Champ optionnel.
     * </p>
     */
    private String prenom;

    /**
     * La nouvelle adresse postale de residence.
     * <p>
     * Champ optionnel. Pris en compte uniquement si la valeur transmise differe de l'etat actuel.
     * </p>
     */
    private String adresse;

    /**
     * Le nouveau numero de telephone de contact.
     * <p>
     * Champ optionnel lors de la soumission du formulaire, mais qui impose neanmoins un controle 
     * syntaxique strict s'il est renseigne : il doit respecter la norme internationale E.164 
     * (prefixe facultatif suivi de 7 a 14 chiffres).
     * </p>
     */
    @Pattern(
            regexp = "^\\+?[1-9]\\d{7,14}$",
            message = "Numéro de téléphone invalide (format international requis)"
        )
    private String telephone;

    /**
     * Le nouveau pays de residence de l'utilisateur.
     * <p>
     * Champ optionnel.
     * </p>
     */
    private String pays;

    /**
     * Le bloc de texte mis a jour pour les preferences ou besoins specifiques du client.
     * <p>
     * Champ optionnel.
     * </p>
     */
    private String preferences;
}