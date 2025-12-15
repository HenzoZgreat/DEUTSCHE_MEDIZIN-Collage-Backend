package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.BatchClassYearSemester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Course;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentCourseScore;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseScoreRepo extends JpaRepository<StudentCourseScore, Long> {

    boolean existsByStudentAndCourseAndBatchClassYearSemester(User student, Course course, BatchClassYearSemester batchClassYearSemester);

    Optional<StudentCourseScore> findByStudentAndCourseAndBatchClassYearSemester(User student, Course course, BatchClassYearSemester batchClassYearSemester);

    List<StudentCourseScore> findByStudentAndIsReleasedTrue(User student);
    
    /**
     * Finds all student course scores for a specific student and batch-class-year-semester.
     * Uses JOIN FETCH to eagerly load necessary relationships and avoid circular reference issues.
     * @param student The student
     * @param batchClassYearSemester The batch-class-year-semester
     * @return List of StudentCourseScore
     */
    @Query("SELECT DISTINCT scs FROM StudentCourseScore scs " +
           "JOIN FETCH scs.course " +
           "JOIN FETCH scs.batchClassYearSemester bcys " +
           "JOIN FETCH bcys.classYear " +
           "JOIN FETCH bcys.semester " +
           "WHERE scs.student = :student AND scs.batchClassYearSemester = :batchClassYearSemester")
    List<StudentCourseScore> findByStudentAndBatchClassYearSemester(@Param("student") User student, @Param("batchClassYearSemester") BatchClassYearSemester batchClassYearSemester);
    
    /**
     * Finds all released student course scores for a specific student, ordered by class start date.
     * Used for CGPA calculation (all courses from enrollment until requested semester).
     * @param student The student
     * @return List of released StudentCourseScore ordered by classStart_GC
     */
    @Query("SELECT scs FROM StudentCourseScore scs " +
            "WHERE scs.student = :student AND scs.isReleased = true " +
            "ORDER BY scs.batchClassYearSemester.classStart_GC ASC")
    List<StudentCourseScore> findByStudentAndIsReleasedTrueOrderedByClassStart(@Param("student") User student);
}
