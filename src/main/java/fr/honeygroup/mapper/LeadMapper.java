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

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(source = "specificDetails", target = "specificDetails", qualifiedByName = "mapDetailsList")
    LeadResponse toResponse(DemandeLead lead);

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