package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.SchoolBackground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolBackgroundRepository extends JpaRepository<SchoolBackground, Long> {
    Optional<SchoolBackground> findByBackground(String background);
}