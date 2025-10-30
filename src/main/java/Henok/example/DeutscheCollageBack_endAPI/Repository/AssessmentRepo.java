package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Assessment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherCourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRepo extends JpaRepository<Assessment, Long> {
    List<Assessment> findByTeacherCourseAssignment(TeacherCourseAssignment tca);
}
