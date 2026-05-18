package fr.honeygroup.repository;

import fr.honeygroup.bo.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Dépôt de données (Repository) Spring Data JPA dédié à la persistance et à la gestion de l'entité {@link Profile}.
 * <p>
 * Ce composant administre les métadonnées et informations complémentaires privées (coordonnées, 
 * préférences, avatars anonymisés) rattachées aux comptes des utilisateurs de Honey Group.
 * </p>
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * Localise les informations de profil complémentaires à partir de l'identifiant technique de l'utilisateur associé.
     * <p>
     * <strong>Sécurité & Ergonomie :</strong> Cette méthode résout la relation un-à-un (OneToOne) en cascade. 
     * Elle permet au système d'extraire la fiche descriptive exclusive de l'appelant authentifié 
     * sur la base de son ID de session, empêchant tout contournement ou injection d'identifiant de profil tiers.
     * </p>
     * * @param userId Identifiant technique unique de l'entité {@code User} propriétaire du profil.
     * @return Un {@link Optional} englobant le profil s'il est configuré en base de données, ou vide.
     */
    Optional<Profile> findByUserId(Long userId);
}