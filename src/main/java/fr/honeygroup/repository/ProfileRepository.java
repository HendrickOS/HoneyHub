package fr.honeygroup.repository;

import fr.honeygroup.bo.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // Trouver le profil via l'ID de l'utilisateur lié
    Optional<Profile> findByUserId(Long userId);
}