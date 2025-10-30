package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentAssessment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentAssessmentKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentAssessmentRepo extends JpaRepository<StudentAssessment, Long> {
    boolean existsById(StudentAssessmentKey key);
}
