package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.BatchClassYearSemester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Course;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentCourseScore;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseScoreRepo extends JpaRepository<StudentCourseScore, Long> {

    boolean existsByStudentAndCourseAndBatchClassYearSemester(User student, Course course, BatchClassYearSemester batchClassYearSemester);

    Optional<StudentCourseScore> findByStudentAndCourseAndBatchClassYearSemester(User student, Course course, BatchClassYearSemester batchClassYearSemester);

    List<StudentCourseScore> findByStudentAndIsReleasedTrue(User student);
}
