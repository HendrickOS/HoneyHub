package fr.honeygroup.mapper;

import fr.honeygroup.bo.Circuit;
import fr.honeygroup.bo.CoursLangue;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.response.PrestationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface de mapping dediee a la transformation des entites metier en objets de reponse (DTOs).
 * <p>
 * Cette interface utilise MapStruct pour automatiser la conversion entre le modele persistant 
 * (entites JPA) et le modele expose (PrestationResponse). Elle gere nativement le polymorphisme 
 * des prestations (Circuits, Cours de langue ou Prestations generiques) via une logique de 
 * dispatching implementee en methode par defaut.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface PrestationMapper {

    /**
     * Mappe une prestation generique en objet de reponse.
     * @param prestation L'entite a mapper.
     * @return La reponse structuree correspondant a la prestation.
     */
    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(target = "type", constant = "GENERIQUE")
    PrestationResponse toResponse(Prestation prestation);

    /**
     * Mappe une entite de type Circuit en objet de reponse.
     * @param circuit L'entite circuit a mapper.
     * @return La reponse structuree avec les attributs specifiques aux circuits.
     */
    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(target = "type", constant = "CIRCUIT")
    PrestationResponse toResponse(Circuit circuit);

    /**
     * Mappe une entite de type CoursLangue en objet de reponse.
     * @param coursLangue L'entite cours de langue a mapper.
     * @return La reponse structuree avec les attributs specifiques aux cours.
     */
    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(target = "type", constant = "COURS_LANGUE")
    PrestationResponse toResponse(CoursLangue coursLangue);

    /**
     * Methode deleguee assurant le polymorphisme lors du mapping.
     * <p>
     * Analyse dynamiquement le type de l'instance de prestation pour appeler le mapping 
     * specialise approprie.
     * </p>
     * @param prestation L'entite source.
     * @return L'instance de PrestationResponse peuplee selon le sous-type detecte.
     */
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