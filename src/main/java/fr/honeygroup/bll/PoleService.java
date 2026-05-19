package fr.honeygroup.bll;

import java.util.List;

import fr.honeygroup.bo.request.PoleRequest;
import fr.honeygroup.bo.response.PoleResponse;

/**
 * Contrat d'interface definissant la logique metier associee aux poles d'activite.
 * <p>
 * Ce service orchestre les regles de gestion des structures organisationnelles de Honey Group 
 * (Poles IT, Ecotourisme, etc.), permettant d'isoler la logique de persistance des regles metiers.
 * </p>
 */
public interface PoleService {

    /**
     * Enregistre un nouveau pole d'activite au sein du systeme.
     * <p>
     * Verifie la conformite des donnees structurelles du pole avant sa persistance en base.
     * </p>
     * * @param request Le DTO de requete contenant les attributs de configuration du nouveau pole.
     * @return Le DTO PoleResponse formalisant la creation effective de la ressource.
     */
    PoleResponse create(PoleRequest request);

    /**
     * Recupere la liste exhaustive de l'ensemble des poles d'activite references.
     * <p>
     * Cette extraction unifiee sert generalement a alimenter les menus de navigation du Front-end.
     * </p>
     * * @return Une liste de DTOs PoleResponse representant la structure globale de l'entreprise.
     */
    List<PoleResponse> getAll();

    /**
     * Recherche un pole d'activite specifique via son identifiant unique.
     * <p>
     * Permet d'obtenir la configuration et le detail d'un pole pour l'analyse de ses dependances.
     * </p>
     * * @param id L'identifiant unique du pole recherche.
     * @return Le DTO PoleResponse associe a la ressource trouvee.
     */
    PoleResponse getById(Long id);

    /**
     * Met a jour les metadonnees et attributs d'un pole d'activite existant.
     * <p>
     * Applique les modifications soumises sur la structure apres validation des contraintes du payload.
     * </p>
     * * @param id L'identifiant unique du pole a modifier.
     * @param request Le DTO PoleRequest contenant les nouvelles valeurs a appliquer.
     * @return Le DTO PoleResponse mis a jour representant le nouvel etat de la ressource.
     */
    PoleResponse update(Long id, PoleRequest request);

    /**
     * Recherche et extrait un pole d'activite a partir de son libelle ou de son nom unique.
     * <p>
     * Regle de gestion permettant de verifier l'unicite d'un pole ou d'aiguiller les requetes 
     * d'affichage basees sur des criteres textuels du catalogue.
     * </p>
     * * @param nom Le libelle exact ou le nom du pole recherche en base de donnees.
     * @return Le DTO PoleResponse correspondant au pole identifie par son nom.
     */
    PoleResponse getByNom(String nom);

    /**
     * Supprime definitivement un pole d'activite du systeme d'information via son identifiant.
     * <p>
     * Cette action critique verifie les contraintes d'integrite pour interdire la suppression 
     * si le pole cible est actuellement lie a des prestations ou des leads actifs.
     * </p>
     * * @param id L'identifiant unique du pole a supprimer.
     */
    void deleteById(Long id);
}