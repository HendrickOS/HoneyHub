package fr.honeygroup.repository;

import fr.honeygroup.bo.PasswordResetToken;
import fr.honeygroup.bo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Depot de donnees (Repository) Spring Data JPA dedie a la gestion persistante des jetons 
 * de reinitialisation de mot de passe.
 * <p>
 * Ce composant facilite la recherche des jetons lors de la validation d'une demande 
 * de recuperation et assure le nettoyage des jetons obsoletes associes a un utilisateur.
 * </p>
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Recherche un jeton de reinitialisation unique par sa valeur textuelle.
     * @param token La chaine de caracteres representative du jeton de securite.
     * @return Un {@link Optional} contenant le jeton s'il est valide et trouve, ou vide.
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Supprime de la base de donnees tous les jetons de reinitialisation lies a un utilisateur donne.
     * <p>
     * Cette methode est utilisee pour invalider les anciennes demandes lorsqu'une nouvelle 
     * procedure de recuperation est initiee ou apres une reinitialisation reussie.
     * </p>
     * @param user L'entite utilisateur dont les jetons doivent etre supprimes.
     */
    void deleteByUser(User user);
}