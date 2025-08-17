package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.ClassYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassYearRepository extends JpaRepository<ClassYear, Long> {
    Optional<ClassYear> findByClassYear(String classYear);
}