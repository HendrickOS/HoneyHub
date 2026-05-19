package fr.honeygroup.repository;

import fr.honeygroup.bo.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Depot de donnees (Repository) Spring Data JPA dedie a la persistance et a la gestion de l'entite {@link Profile}.
 * <p>
 * Ce composant administre les metadonnees et informations complementaires privees 
 * (coordonnees, preferences) rattachees aux comptes des utilisateurs de Honey Group.
 * </p>
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * Localise les informations de profil complementaires a partir de l'identifiant technique 
     * de l'utilisateur associe.
     * <p>
     * Securite et Ergonomie : Cette methode resout la relation un-a-un (OneToOne) en cascade. 
     * Elle permet au systeme d'extraire la fiche descriptive exclusive de l'appelant authentifie 
     * sur la base de son ID de session, empechant tout contournement ou injection d'identifiant 
     * de profil tiers.
     * </p>
     * @param userId Identifiant technique unique de l'entite {@code User} proprietaire du profil.
     * @return Un {@link Optional} englobant le profil s'il est configure en base de donnees, ou vide.
     */
    Optional<Profile> findByUserId(Long userId);

    /**
     * Verifie l'unicite ou l'existence d'un numero de telephone dans le referentiel des profils.
     * @param telephone Le numero de telephone a verifier.
     * @return true si le numero est deja utilise par un profil, false sinon.
     */
    boolean existsByTelephone(String telephone);
}