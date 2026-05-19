package fr.honeygroup.repository;

import fr.honeygroup.bo.RefreshToken;
import fr.honeygroup.bo.User;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA dedie a la gestion persistante des jetons de rafraichissement (Refresh Tokens).
 * <p>
 * Ce composant offre les operations CRUD standard via {@link JpaRepository} ainsi que des 
 * methodes personnalisees pour la recherche par valeur de jeton et le nettoyage automatique 
 * des jetons associes a un utilisateur specifique.
 * </p>
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    /**
     * Recherche un jeton de rafraichissement par sa valeur textuelle unique.
     * @param token La chaine de caracteres correspondant au jeton.
     * @return Un Optional contenant le jeton s'il est trouve, ou vide sinon.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Supprime de la base de donnees l'ensemble des jetons de rafraichissement associes a un utilisateur donne.
     * <p>
     * Cette operation est marquee comme @Modifying et @Transactional car elle effectue une 
     * requete de suppression personnalisee (DELETE) plutot qu'une simple lecture.
     * </p>
     * @param user L'entite utilisateur dont les jetons doivent etre invalides.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.user = :user")
    void deleteByUser(@Param("user") User user);
}