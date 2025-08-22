package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseRepo extends JpaRepository<StudentCourse, Long> {
    boolean existsByStudentIdAndCourseIdAndBatchClassYearSemesterId(Long studentId, Long courseId, Long batchClassYearSemesterId);
    Optional<StudentCourse> findByStudentIdAndCourseIdAndBatchClassYearSemesterId(Long studentId, Long courseId, Long batchClassYearSemesterId);
    List<StudentCourse> findByStudentIdAndIsReleasedTrue(Long studentId);
}