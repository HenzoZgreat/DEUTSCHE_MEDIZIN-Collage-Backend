package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.BatchClassYearSemester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Course;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherCourseAssignment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherCourseAssignmentRepository extends JpaRepository<TeacherCourseAssignment, Long> {
    List<TeacherCourseAssignment> findByTeacher(TeacherDetail teacher);

    boolean existsByTeacherAndCourseAndBcys(TeacherDetail teacher, Course course, BatchClassYearSemester bcys);

    // Count students in the same BCYS (since no StudentEnrollment yet)
    // In TeacherCourseAssignmentRepository.java
    @Query("""
    SELECT COUNT(DISTINCT scs.student)
    FROM StudentCourseScore scs
    WHERE scs.course = :course
      AND scs.batchClassYearSemester = :bcys
    """)
    Long countEnrolledStudents(@Param("course") Course course, @Param("bcys") BatchClassYearSemester bcys);

    List<TeacherCourseAssignment> findByCourse(Course course);
}