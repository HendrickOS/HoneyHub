package fr.honeygroup.bll.impl;

import fr.honeygroup.bll.UserService;
import fr.honeygroup.bo.Profile;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.ProfileUpdateRequest;
import fr.honeygroup.bo.response.UserProfileResponse;
import fr.honeygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation du service metier destine a la gestion des utilisateurs et de leurs profils.
 * <p>
 * Cette classe realise les operations de lecture de l'identite contextuelle et applique 
 * les modifications sur les fiches profils. Elle encapsule la logique transactionnelle
 * et orchestre la transition vers les objets de transfert de donnees (DTO).
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /** Depot de donnees pour la persistance et l'extraction des entites User. */
    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     * <p>
     * Extrait l'utilisateur depuis la base de donnees via son adresse email cryptographique.
     * Levee d'une exception de securite si le compte n'est pas identifie dans le referentiel.
     * </p>
     * * @throws RuntimeException Si aucun utilisateur ne correspond a l'adresse email fournie.
     */
    @Override
    public UserProfileResponse getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Execute l'operation dans un contexte transactionnel global. Cette methode verifie 
     * les modifications soumises, initialise a la volee une entite Profile orpheline 
     * si elle n'existe pas (relation 1:1), et persiste l'etat consolide de l'utilisateur.
     * </p>
     * * @throws RuntimeException Si l'utilisateur a modifier n'existe pas dans le systeme.
     */
    @Override
    @Transactional
    public UserProfileResponse updateProfile(String email, ProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Mise a jour conditionnelle des donnees d'identite de l'utilisateur
        if (request.getNom() != null && !request.getNom().isEmpty()) {
            user.setNom(request.getNom());
        }
        if (request.getPrenom() != null && !request.getPrenom().isEmpty()) {
            user.setPrenom(request.getPrenom());
        }

        // Initialisation securisee du profil rattache (Relation OneToOne)
        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
            user.setProfile(profile);
        }

        // Hydratation des attributs optionnels du profil
        if (request.getAdresse() != null) profile.setAdresse(request.getAdresse());
        if (request.getTelephone() != null) profile.setTelephone(request.getTelephone());
        if (request.getPays() != null) profile.setPays(request.getPays());
        if (request.getPreferences() != null) profile.setPreferences(request.getPreferences());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    /**
     * Methode utilitaire interne assurant le mapping d'une entite metier vers son DTO de reponse.
     * <p>
     * Extrait les donnees croisees de l'User et de son Profile associe en securisant les cas 
     * de references nulles (Null-Safe), puis assemble le DTO via le pattern Builder de Lombok.
     * </p>
     * * @param user L'entite source complete a convertir.
     * @return Le DTO UserProfileResponse normalise pour la couche d'exposition REST.
     */
    private UserProfileResponse mapToResponse(User user) {
        Profile profile = user.getProfile();
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole().name())
                .adresse(profile != null ? profile.getAdresse() : null)
                .telephone(profile != null ? profile.getTelephone() : null)
                .pays(profile != null ? profile.getPays() : null)
                .preferences(profile != null ? profile.getPreferences() : null)
                .build();
    }
}