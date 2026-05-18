package fr.honeygroup.mapper;

import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.DetailsSpecifiques;
import fr.honeygroup.bo.response.LeadResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Composant de mapping MapStruct gérant l'aplatissement et la transformation 
 * des opportunités commerciales (DemandeLead).
 * <p>
 * Ce mapper extrait de manière sécurisée les clés étrangères et les libellés de l'arbre relationnel. 
 * Il intègre un mécanisme personnalisé de pivotement pour convertir la structure relationnelle 
 * des détails spécifiques en dictionnaire de chaînes exploitable directement par le Frontend.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface LeadMapper {

    /**
     * Transforme l'entité complexe {@link DemandeLead} en son DTO de sortie épuré {@link LeadResponse}.
     * <p>
     * Les propriétés des sous-entités associées (User, Pole, Prestation) sont projetées à plat. 
     * La collection de lignes de détails est retraitée via la méthode qualifiée {@code mapDetailsList}.
     * </p>
     * * @param lead L'entité de persistance source contenant l'historique et les caractéristiques du lead.
     * @return Le DTO de réponse configuré, aplati et prêt pour l'exposition de l'API.
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(source = "prestation.id", target = "prestationId")
    @Mapping(source = "prestation.titreService", target = "prestationTitre")
    @Mapping(source = "specificDetails", target = "specificDetails", qualifiedByName = "mapDetailsList")
    LeadResponse toResponse(DemandeLead lead);

    /**
     * Méthode de conversion personnalisée (Custom Mapping Helper) nommée et imbriquée.
     * <p>
     * Pivote une collection d'entités {@link DetailsSpecifiques} en une instance {@link Map} standard. 
     * Elle intègre une sécurité (merge function) pour écraser et conserver la dernière valeur en cas 
     * de collision de clés, et gère de manière défensive les valeurs nulles.
     * </p>
     * * @param details La liste d'entités de détails spécifiques extraites par Hibernate.
     * @return Une structure {@link Map} associant chaque libellé de clé à sa valeur textuelle.
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
                        (existing, replacement) -> replacement // Résolution de conflit en cas de clés doublonnées
                ));
    }
}