package fr.honeygroup.repository;

import fr.honeygroup.bo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * INDISPENSABLE pour Spring Security.
     * On cherche l'utilisateur par son email (qui sert de login).
     */
    Optional<User> findByEmail(String email);

    /**
     * Pour vérifier si un email est déjà pris lors de l'inscription.
     */
    boolean existsByEmail(String email);

    /**
     * Pour ton interface Admin : chercher un client par son nom ou prénom.
     */
    List<User> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    /**
     * Pour filtrer les utilisateurs par rôle (ex: lister tous les ADMINS).
     */
    List<User> findByRole(String role);
}