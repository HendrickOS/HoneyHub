package fr.honeygroup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.request.PoleRequest;
import fr.honeygroup.bo.response.PoleResponse;

/**
 * Composant de mapping MapStruct gérant les conversions bidirectionnelles 
 * pour l'entité des pôles d'activité (Pole).
 * <p>
 * L'annotation {@code componentModel = "spring"} configure MapStruct pour générer une implémentation 
 * concrète enregistrée en tant que Bean Spring (composant injectable). Cela permet de dissocier 
 * proprement le modèle persistant (BO) des structures d'exposition de l'API (DTOs Request/Response).
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PoleMapper {

    /**
     * Transforme une entité persistante {@link Pole} en un objet de transfert de données épuré {@link PoleResponse}.
     * <p>
     * Cette conversion est exploitée lors des phases de consultation (Sortie API) pour n'exposer 
     * que les attributs nécessaires aux interfaces graphiques.
     * </p>
     * * @param pole L'entité source issue de la base de données MariaDB.
     * @return Le DTO de réponse correspondant, prêt pour la sérialisation JSON.
     */
    PoleResponse toResponse(Pole pole);

    /**
     * Convertit un objet de transfert de données entrant {@link PoleRequest} en une entité métier {@link Pole}.
     * <p>
     * Cette méthode intervient en entrée d'API lors des requêtes de création ou de mise à jour 
     * afin de reconstruire un graphe d'objet manipulable par les couches de persistance.
     * </p>
     * * @param request Le DTO de requête source validé en provenance du Frontend.
     * @return L'entité cible prête à être traitée par la couche BLL puis persistée par le dépôt.
     */
    Pole toEntity(PoleRequest request);
}