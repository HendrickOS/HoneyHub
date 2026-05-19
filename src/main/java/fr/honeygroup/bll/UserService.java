package fr.honeygroup.bll;

import fr.honeygroup.bo.request.ProfileUpdateRequest;
import fr.honeygroup.bo.response.UserProfileResponse;

/**
 * Contrat d'interface definissant la logique metier associee a la gestion des utilisateurs.
 * <p>
 * Ce service formalise les operations de lecture et de modification des donnees de profil
 * des utilisateurs authentifies, en assurant la separation entre la couche d'exposition (API)
 * et la couche d'acces aux donnees (DAO).
 * </p>
 */
public interface UserService {

    /**
     * Recupere les informations detaillees du profil de l'utilisateur connecte a partir de son identifiant unique (email).
     * <p>
     * L'adresse de messagerie est extraite en amont du contexte de securite de Spring Security (Token JWT).
     * </p>
     * * @param email L'adresse email unique de l'utilisateur extrait du jeton de session.
     * @return Le DTO UserProfileResponse contenant les informations filtrees et securisees du profil.
     */
    UserProfileResponse getCurrentUserProfile(String email);

    /**
     * Applique les modifications de donnees soumises sur le profil de l'utilisateur connecte.
     * <p>
     * Realise la mise a jour des champs autorises (coordonnees, informations personnelles) apres 
     * validation de la requete, tout en preservant l'etancheite des informations privees.
     * </p>
     * * @param email L'adresse email unique de l'utilisateur pour identifier le compte cible.
     * @param request Le DTO ProfileUpdateRequest contenant les nouvelles valeurs verifiees par le systeme.
     * @return Le DTO UserProfileResponse mis a jour representant le nouvel etat du profil.
     */
    UserProfileResponse updateProfile(String email, ProfileUpdateRequest request);
}