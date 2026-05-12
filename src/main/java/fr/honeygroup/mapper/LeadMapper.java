package fr.honeygroup.mapper;

import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.DetailsSpecifiques;
import fr.honeygroup.bo.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LeadMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(source = "prestation.id", target = "prestationId")
    @Mapping(source = "prestation.titreService", target = "prestationTitre")
    // MapStruct va chercher la méthode qualifiedByName pour transformer la liste en Map
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
                        (existing, replacement) -> replacement // Sécurité si deux clés sont identiques
                ));
    }
}