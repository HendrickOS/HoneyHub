package fr.honeygroup.bll;

import fr.honeygroup.bo.request.CircuitRequest;
import fr.honeygroup.bo.request.CoursLangueRequest;
import fr.honeygroup.bo.request.PrestationRequest;
import fr.honeygroup.bo.response.PrestationResponse;

import java.util.List;

/**
 * Contrat d'interface definissant la logique metier associee a la gestion du catalogue.
 * <p>
 * Ce service centralise les regles de gestion, de creation polymorphe et de suppression 
 * des differentes offres de services de Honey Group (Prestations de base, Circuits et Cours).
 * </p>
 */
public interface PrestationService {

    /**
     * Extrait l'integralite des prestations actives du catalogue commercial.
     * <p>
     * Assure la recuperation unifiee des donnees generiques et specifiques pour l'affichage Front-end.
     * </p>
     * * @return Une liste de DTOs PrestationResponse representant l'ensemble du catalogue.
     */
    List<PrestationResponse> getAllPrestations();

    /**
     * Recherche une prestation specifique a partir de son identifiant unique.
     * <p>
     * Permet d'obtenir le detail complet d'une offre pour la preparation d'un devis ou d'une reservation.
     * </p>
     * * @param id L'identifiant unique de la prestation recherchee.
     * @return Le DTO PrestationResponse correspondant a la ressource trouvee.
     */
    PrestationResponse getPrestationById(Long id);

    /**
     * Traite et persiste une nouvelle prestation de nature generique (sans options additionnelles).
     * <p>
     * Applique les verifications de conformite commerciale de base avant insertion.
     * </p>
     * * @param request Le DTO de requete contenant les attributs structurels de l'offre.
     * @return Le DTO PrestationResponse formalisant l'enregistrement de la nouvelle ressource.
     */
    PrestationResponse createPrestationGenerique(PrestationRequest request);

    /**
     * Traite et persiste une prestation specifique de type "Circuit".
     * <p>
     * Cette methode orchestre la creation conjointe de la prestation de base et des donnees 
     * logistiques propres au circuit (etapes, trajets) en respectant les contraintes d'integrite.
     * </p>
     * * @param request Le DTO CircuitRequest embarquant les metadonnees et attributs du circuit.
     * @return Le DTO PrestationResponse incluant les donnees et les specificites du circuit cree.
     */
    PrestationResponse createCircuit(CircuitRequest request);

    /**
     * Traite et persiste une prestation specifique de type "Cours de langue".
     * <p>
     * Assure l'association stricte entre le socle de prestation globale et les parametres 
     * pedagogiques (volume horaire, specifications de la langue cible).
     * </p>
     * * @param request Le DTO CoursLangueRequest contenant la configuration pedagogique de l'offre.
     * @return Le DTO PrestationResponse incluant les donnees et les specificites du cours cree.
     */
    PrestationResponse createCoursLangue(CoursLangueRequest request);

    /**
     * Supprime definitivement une offre de prestation du catalogue general.
     * <p>
     * Cette operation declenche la verification des regles de dependances metier pour empecher 
     * la suppression d'une offre actuellement liee a des sessions actives ou des reservations.
     * </p>
     * * @param id L'identifiant unique de la prestation a radier du catalogue.
     */
    void deletePrestation(Long id);
}