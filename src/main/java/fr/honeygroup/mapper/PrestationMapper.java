package fr.honeygroup.mapper;

import fr.honeygroup.bo.Circuit;
import fr.honeygroup.bo.CoursLangue;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.response.PrestationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrestationMapper {

    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(target = "type", constant = "GENERIQUE")
    PrestationResponse toResponse(Prestation prestation);

    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(target = "type", constant = "CIRCUIT")
    PrestationResponse toResponse(Circuit circuit);

    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(target = "type", constant = "COURS_LANGUE")
    PrestationResponse toResponse(CoursLangue coursLangue);

    default PrestationResponse toGenericResponse(Prestation prestation) {
        if (prestation instanceof Circuit) {
            return toResponse((Circuit) prestation);
        } else if (prestation instanceof CoursLangue) {
            return toResponse((CoursLangue) prestation);
        } else {
            return toResponse(prestation);
        }
    }
}
