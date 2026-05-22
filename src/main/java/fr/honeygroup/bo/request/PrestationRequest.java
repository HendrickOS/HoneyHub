package fr.honeygroup.bo.request;

import java.util.HashMap;
import java.util.Map;

import fr.honeygroup.enumeration.StatutPrestation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Objet de transfert de donnees (DTO) socle pour la creation ou modification d'une prestation.
 * <p>
 * Cette classe sert de structure racine pour l'exposition du catalogue d'offres de Honey Group.
 * Elle exploite l'annotation {@link SuperBuilder} pour permettre aux DTOs derives (Circuits, Cours) 
 * d'heriter proprement de la mecanique de construction par chainage des attributs communs.
 * </p>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PrestationRequest {
    
    /**
     * Identifiant technique unique du pole organisationnel auquel est rattachee la prestation.
     * <p>
     * Contrainte : Requis, sert de point d'ancrage pour l'association relationnelle de l'offre.
     * </p>
     */
    @NotNull(message = "Le pôle est obligatoire")
    private Long poleId;

    /**
     * Le titre ou le libelle commercial visible de la prestation.
     * <p>
     * Contraintes : Obligatoire, compris de maniere stricte entre 3 et 255 caracteres.
     * </p>
     */
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 255)
    private String titreService;

    /**
     * Bloc textuel detaillant les contours et le descriptif de la prestation.
     * <p>
     * Contraintes : Obligatoire, taille bornee entre 10 et 2000 caracteres pour assurer 
     * un contenu riche pour le client final.
     * </p>
     */
    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 2000)
    private String description;
    
    /**
     * Tarification initiale ou prix de base applique a la prestation.
     * <p>
     * Contrainte : Obligatoire. Represente sous forme de Double pour supporter les valeurs decimales.
     * </p>
     */
    @NotNull(message = "Le prix de base est obligatoire")
    private Double prixBase;

    /**
     * Statut de publication et d'accessibilite de l'offre dans le catalogue (ex: ACTIF, ARCHIVE).
     * <p>
     * Champ optionnel. Si omis lors de la soumission, la couche BLL y injectera la valeur 
     * active par defaut.
     * </p>
     */
    private StatutPrestation statut;
    
    /**
     * Données techniques flexibles initialisées lors de la création de la prestation.
     * Permet de définir les spécificités du pôle dès l'instanciation de l'offre.
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
}