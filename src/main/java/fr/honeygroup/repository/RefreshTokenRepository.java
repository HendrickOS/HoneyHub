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

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	 Optional<RefreshToken> findByToken(String token);

	    @Modifying
	    @Transactional
	    @Query("DELETE FROM RefreshToken r WHERE r.user = :user")
	    void deleteByUser(@Param("user") User user);
}
