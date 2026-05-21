package fr.honeygroup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.request.SessionRequest;
import fr.honeygroup.bo.response.SessionResponse;

/**
 * Mapper pour la conversion entre l'entité Session et ses objets de transfert (DTOs).
 * Utilise la bibliothèque MapStruct pour générer automatiquement les implémentations.
 */
@Mapper(componentModel = "spring")
public interface SessionMapper {

    /**
     * Convertit une entité Session en DTO de réponse.
     * <p>
     * Note : {@code participantsActuels} est mappé depuis {@code nbInscrits} de l'entité.
     * </p>
     */
    @Mapping(target = "participantsActuels", source = "nbInscrits")
    @Mapping(target = "statut", source = "statutSession")
    SessionResponse toResponse(Session session);

    /**
     * Convertit une requête entrante en entité Session.
     */
    @Mapping(target = "statutSession", source = "statut")
    Session toEntity(SessionRequest request);
}