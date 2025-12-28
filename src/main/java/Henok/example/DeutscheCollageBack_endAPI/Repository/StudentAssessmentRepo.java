package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Assessment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentAssessment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentAssessmentKey;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAssessmentRepo extends JpaRepository<StudentAssessment, Long> {
    boolean existsById(StudentAssessmentKey key);

    // Finds a specific score record (used in update and view)
    // Why: Optional return allows us to return null score in the grid view
    Optional<StudentAssessment> findById(StudentAssessmentKey id);

    List<StudentAssessment> findByAssessmentInAndStudentIn(
            List<Assessment> assessments, List<StudentDetails> students);
}
