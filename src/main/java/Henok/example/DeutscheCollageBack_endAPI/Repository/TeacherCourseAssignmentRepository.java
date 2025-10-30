package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherCourseAssignment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherCourseAssignmentRepository extends JpaRepository<TeacherCourseAssignment, Long> {
    List<TeacherCourseAssignment> findByTeacher(TeacherDetail teacher);
}