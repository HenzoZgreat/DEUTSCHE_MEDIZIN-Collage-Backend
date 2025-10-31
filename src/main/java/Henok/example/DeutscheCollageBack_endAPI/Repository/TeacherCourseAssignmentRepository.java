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
    @Query("SELECT COUNT(s) FROM StudentDetails s WHERE s.batchClassYearSemester = :bcys")
    Long countStudentsInBcys(@Param("bcys") BatchClassYearSemester bcys);
}