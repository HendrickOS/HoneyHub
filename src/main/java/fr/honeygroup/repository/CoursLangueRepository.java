package fr.honeygroup.repository;

import fr.honeygroup.bo.CoursLangue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursLangueRepository extends JpaRepository<CoursLangue, Long> {
}
