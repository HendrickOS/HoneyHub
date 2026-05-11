package fr.honeygroup.mapper;

import org.mapstruct.Mapper;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.request.*;
import fr.honeygroup.bo.response.PoleResponse;

@Mapper(componentModel = "spring")
public interface PoleMapper {

    // BO -> Response (Sortie API)
    PoleResponse toResponse(Pole pole);

    // Request -> BO (Entrée API / Création)
    Pole toEntity(PoleRequest request);
}