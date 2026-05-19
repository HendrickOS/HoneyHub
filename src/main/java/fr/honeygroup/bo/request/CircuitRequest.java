package fr.honeygroup.bo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Objet de transfert de donnees (DTO) specifique destine a la creation ou modification d'un circuit touristique.
 * <p>
 * Cette classe etend {@link PrestationRequest} pour heriter du socle commun des offres de Honey Group.
 * Elle applique {@link EqualsAndHashCode} avec appel au parent pour securiser la comparaison d'objets,
 * et s'appuie sur {@link SuperBuilder} pour collecter l'ensemble des parametres lors de l'instanciation.
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CircuitRequest extends PrestationRequest {

    /**
     * Descriptif commercial enrichi et exhaustif de l'offre de voyage ou de l'excursion.
     * <p>
     * Contraintes : Obligatoire, taille encadree de maniere stricte entre 20 et 5000 caracteres 
     * pour fournir un maximum de details d'immersion au client final.
     * </p>
     */
    @NotBlank(message = "La description longue est obligatoire")
    @Size(min = 20, max = 5000)
    private String descriptionLongue;

    /**
     * Descriptif textuel des etapes, escales, points d'interet et parcours composant le circuit.
     * <p>
     * Contrainte : Obligatoire, ne peut pas etre nul ou vide.
     * </p>
     */
    @NotBlank(message = "L'itinéraire est obligatoire")
    private String itineraire;

    /**
     * Expression de la periode temporelle ou du format de sejour de l'offre.
     * <p>
     * Contrainte : Obligatoire, generalement formalisee sous forme de chaine explicite 
     * (ex: "7 jours / 6 nuits", "Week-end complet").
     * </p>
     */
    @NotBlank(message = "La durée est obligatoire (ex: 7 jours / 6 nuits)")
    private String duree;
}