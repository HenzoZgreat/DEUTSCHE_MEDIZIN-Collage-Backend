package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentAssessmentRepo extends JpaRepository<StudentAssessment, Long> {
}
