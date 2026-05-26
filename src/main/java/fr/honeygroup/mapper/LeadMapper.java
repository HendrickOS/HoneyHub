package fr.honeygroup.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.DetailsSpecifiques;
import fr.honeygroup.bo.response.LeadResponse;

/**
 * Composant de mapping MapStruct gérant l'aplatissement et la transformation 
 * des opportunités commerciales (DemandeLead) vers leurs DTO de sortie.
 * <p>
 * Ce mapper extrait de manière sécurisée les clés étrangères et les libellés de l'arbre relationnel. 
 * Il intègre un mécanisme personnalisé de pivotement pour convertir la structure relationnelle 
 * des détails spécifiques en dictionnaire de chaînes exploitable directement par le Frontend.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface LeadMapper {

    /**
     * Convertit une entité transactionnelle complexe de persistance en un DTO de réponse épuré et plat.
     * <p>
     * Cette méthode configure l'extraction des données d'identité de l'émetteur (gérant le cas client et visiteur),
     * résout les dépendances du pôle cible et délègue la transformation de la collection d'attributs dynamiques.
     * </p>
     *
     * @param lead L'entité maîtresse {@link DemandeLead} chargée depuis la base de données.
     * @return Un {@link LeadResponse} optimisé pour l'affichage de l'interface utilisateur.
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(source = "pole.nom", target = "poleNom") // Fix : Mappe le nom clair du pôle pour le frontend
    @Mapping(source = "nomContact", target = "nomContact") // Fix : Assure le transfert pour les visiteurs anonymes
    @Mapping(source = "emailContact", target = "emailContact") // Fix : Assure le transfert pour les visiteurs anonymes
    @Mapping(target = "userNomComplet", expression = "java(lead.getUser() != null ? lead.getUser().getNom() + \" \" + lead.getUser().getPrenom() : null)") // Fix : Concaténation sécurisée de l'identité client
    @Mapping(source = "specificDetails", target = "specificDetails", qualifiedByName = "mapDetailsList")
    LeadResponse toResponse(DemandeLead lead);

    /**
     * Méthode de mapping personnalisée (Named Hooks) transformant une liste relationnelle d'entités 
     * en une Map associative plate (Pattern Entity-Attribute-Value).
     * <p>
     * Sécurise l'extraction en gérant les collections nulles ou vides et en appliquant une politique 
     * de résolution des conflits de clés en cas de doublons (le dernier élément remplace le précédent).
     * </p>
     *
     * @param details La liste d'entités {@link DetailsSpecifiques} associées au dossier parent.
     * @return Une {@link Map} de paires clé/valeur au format chaîne de caractères, ou un dictionnaire vide.
     */
    @Named("mapDetailsList")
    default Map<String, String> mapDetailsList(List<DetailsSpecifiques> details) {
        if (details == null || details.isEmpty()) {
            return Collections.emptyMap();
        }

        return details.stream()
                .collect(Collectors.toMap(
                        DetailsSpecifiques::getChampCle,
                        d -> d.getValeur() != null ? d.getValeur() : "",
                        (existing, replacement) -> replacement
                ));
    }
}