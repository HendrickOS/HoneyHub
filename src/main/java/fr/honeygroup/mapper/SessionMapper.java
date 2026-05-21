package fr.honeygroup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.request.SessionRequest;
import fr.honeygroup.bo.response.SessionResponse;

/**
 * Mapper (MapStruct) dédié à la conversion bidirectionnelle entre l'entité métier {@link Session} 
 * et ses objets de transfert (DTOs : {@link SessionRequest}, {@link SessionResponse}).
 * <p>
 * Ce composant permet d'isoler la logique de transformation des données pour 
 * ne pas polluer la couche de service technique (BLL) ni les contrôleurs, tout en 
 * sécurisant l'injection de dépendances.
 * </p>
 */
//Ignore automatiquement les propriétés de la cible qui ne sont pas mappées
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SessionMapper {

    /**
     * Convertit une entité Session persistée en DTO de réponse pour l'API.
     * <p>
     * Gère l'aplatissement (flattening) des données relationnelles, notamment en 
     * extrayant directement l'ID et le nom de la prestation parente pour 
     * optimiser les performances d'affichage côté client (évite les requêtes N+1).
     * </p>
     *
     * @param session L'entité métier source.
     * @return Le DTO {@link SessionResponse} formaté pour le client.
     */
    @Mapping(target = "participantsActuels", source = "nbInscrits")
    @Mapping(target = "statut", source = "statutSession")
    @Mapping(target = "prestationId", source = "prestation.id")
    @Mapping(target = "prestationNom", source = "prestation.titreService")
    SessionResponse toResponse(Session session);

    /**
     * Convertit un DTO de requête entrante en entité Session.
     * <p>
     * La relation complexe avec l'entité {@code Prestation} est volontairement ignorée 
     * ici, car elle nécessite une validation métier stricte et un chargement depuis la 
     * base de données dans la couche de service ({@code SessionServiceImpl}) 
     * avant d'être attachée à l'entité.
     * </p>
     *
     * @param request Le DTO de requête contenant les données saisies par le manager.
     * @return Une entité {@link Session} instanciée (prête à être enrichie par le service).
     */
    @Mapping(target = "statutSession", source = "statut")
    @Mapping(target = "prestation", ignore = true) // L'entité Prestation sera chargée manuellement dans le service
    @Mapping(target = "nbInscrits", ignore = true) // Initialisé par défaut par l'entité ou la BDD
    @Mapping(target = "id", ignore = true) // Protégé : L'ID est généré par Hibernate
    Session toEntity(SessionRequest request);
}