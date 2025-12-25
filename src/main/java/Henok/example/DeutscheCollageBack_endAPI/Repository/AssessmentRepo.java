package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Assessment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherCourseAssignment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherDetail;
import Henok.example.DeutscheCollageBack_endAPI.Enums.AssessmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AssessmentRepo extends JpaRepository<Assessment, Long> {
    List<Assessment> findByTeacherCourseAssignmentOrderByCreatedAtAsc(TeacherCourseAssignment tca);

    // Counts all assessments for a teacher's assignments
    // Why: Aggregates total assessments created by the teacher across all their courses
    // Handles empty list by returning 0
    @Query("SELECT COUNT(a) FROM Assessment a " +
            "WHERE a.teacherCourseAssignment.teacher = :teacher")
    long countByTeacher(TeacherDetail teacher);

    // Counts pending assessments for a teacher's assignments
    // Why: Shows assessments the teacher has created but are pending approval
    // Handles empty list by returning 0
    @Query("SELECT COUNT(a) FROM Assessment a " +
            "WHERE a.teacherCourseAssignment.teacher = :teacher AND a.assStatus = :status")
    long countByTeacherAndAssStatus(TeacherDetail teacher, AssessmentStatus status);


    // Custom delete to cascade to StudentAssessment
    // Why: JPA @OneToMany with orphanRemoval=true or CascadeType.REMOVE is not enough for composite key
    //      We manually delete child records first to avoid foreign key constraint violation
    @Modifying
    @Transactional
    @Query("DELETE FROM StudentAssessment sa WHERE sa.assessment.assID = :assessmentId")
    void deleteStudentAssessmentsByAssessmentId(@Param("assessmentId") Long assessmentId);

    // Optional: delete all student assessments for a list of assessments
    @Modifying
    @Transactional
    @Query("DELETE FROM StudentAssessment sa WHERE sa.assessment.assID IN :assessmentIds")
    void deleteStudentAssessmentsByAssessmentIds(@Param("assessmentIds") List<Long> assessmentIds);

    List<Assessment> findByTeacherCourseAssignment(TeacherCourseAssignment tca);

    @Query("SELECT a.assID FROM Assessment a WHERE a.teacherCourseAssignment.id = :assignmentId")
    List<Long> findAssessmentIdsByAssignmentId(@Param("assignmentId") Long assignmentId);
}
