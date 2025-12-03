package Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AttritionCause;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AttritionCauseRepository extends JpaRepository<AttritionCause, Long> {
    boolean existsByAttritionCauseIgnoreCase(String attritionCause);
    Optional<AttritionCause> findByAttritionCauseIgnoreCase(String attritionCause);
}