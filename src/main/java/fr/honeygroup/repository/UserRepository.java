package fr.honeygroup.repository;

import fr.honeygroup.bo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Dépôt de données (Repository) Spring Data JPA dédié à la persistance et à la gestion de l'entité {@link User}.
 * <p>
 * Cette interface fournit l'accès aux opérations de base du CRUD sur la table des utilisateurs 
 * et définit les requêtes dérivées (Derived Queries) stratégiques pour l'authentification Spring Security 
 * et l'administration des comptes de Honey Group.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur à partir de son identifiant de connexion unique (adresse email).
     * <p>
     * <strong>Rôle critique :</strong> Cette méthode est le pivot d'interconnexion avec l'infrastructure 
     * de Spring Security (implémentation du {@code UserDetailsService}) pour charger le principal lors 
     * des phases d'authentification et de vérification des jetons (JWT).
     * </p>
     * * @param email L'adresse de courrier électronique servant d'identifiant d'accès.
     * @return Un {@link Optional} contenant l'utilisateur s'il est localisé, ou vide dans le cas contraire.
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie de manière optimisée la présence ou l'absence d'un compte associé à une adresse email.
     * <p>
     * Utilisée de façon défensive au niveau des tunnels d'inscription (Sign Up) pour interdire 
     * les collisions ou les créations de comptes doublonnés. Génère une requête SQL de type {@code EXISTS} 
     * extrêmement performante.
     * </p>
     * * @param email L'adresse email à contrôler.
     * @return {@code true} si l'identifiant est déjà enregistré en base de données, {@code false} sinon.
     */
    boolean existsByEmail(String email);

    /**
     * Recherche floue et multicritère d'utilisateurs par correspondance textuelle sur l'identité civile.
     * <p>
     * Dédiée aux fonctionnalités de filtrage dynamique sur le tableau de bord d'administration (Staff/Backoffice). 
     * L'usage de {@code Containing} applique l'opérateur SQL {@code LIKE %critère%} tandis que 
     * {@code IgnoreCase} neutralise la sensibilité à la casse.
     * </p>
     * * @param nom Fragment ou totalité du nom de famille recherché.
     * @param prenom Fragment ou totalité du prénom recherché.
     * @return Une liste de {@link User} dont le nom ou le prénom s'aligne avec les motifs soumis.
     */
    List<User> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    /**
     * Filtre et extrait la liste des utilisateurs possédant un niveau d'habilitation ou rôle applicatif précis.
     * <p>
     * Permet au système d'isoler des groupes d'acteurs, par exemple pour lister l'intégralité 
     * des profils gestionnaires (ADMIN, MANAGER) au sein du backoffice.
     * </p>
     * * @param role Le libellé textuel du rôle cible (ex: "ROLE_ADMIN").
     * @return Une liste de {@link User} partageant le rôle spécifié.
     */
    List<User> findByRole(String role);
}