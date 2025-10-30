package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssignCoursesRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherCourseAssignmentResponse;
import Henok.example.DeutscheCollageBack_endAPI.Entity.BatchClassYearSemester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Course;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherCourseAssignment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherDetail;
import Henok.example.DeutscheCollageBack_endAPI.Repository.BatchClassYearSemesterRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.CourseRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.TeacherCourseAssignmentRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.TeacherRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherCourseAssignmentService {

    private final TeacherCourseAssignmentRepository assignmentRepo;
    private final TeacherRepository teacherRepo;
    private final CourseRepo courseRepo;
    private final BatchClassYearSemesterRepo bcysRepo;

    private final EntityManager em;

    // Assign multiple courses to a teacher
    public List<TeacherCourseAssignmentResponse> assignCoursesToTeacher(
            Long teacherId, List<AssignCoursesRequest> requests) {

        TeacherDetail teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + teacherId));

        List<TeacherCourseAssignment> assignments = new ArrayList<>();

        for (AssignCoursesRequest req : requests) {
            Course course = courseRepo.findById(req.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + req.getCourseId()));

            BatchClassYearSemester bcys = bcysRepo.findById(req.getBcysId())
                    .orElseThrow(() -> new IllegalArgumentException("BCYS not found with ID: " + req.getBcysId()));

            // Prevent duplicate assignment
            if (assignmentRepo.existsByTeacherAndCourseAndBcys(teacher, course, bcys)) {
                throw new IllegalArgumentException(
                        "Teacher already assigned to course " + course.getCTitle() + " in " + bcys.getDisplayName());
            }

            TeacherCourseAssignment assignment = new TeacherCourseAssignment();
            assignment.setTeacher(teacher);
            assignment.setCourse(course);
            assignment.setBcys(bcys);
            assignment.setAssignedAt(LocalDateTime.now());

            assignments.add(assignment);
        }

        em.flush();
        em.clear();

        List<TeacherCourseAssignment> saved = assignmentRepo.saveAll(assignments);
        return saved.stream()
                .map(TeacherCourseAssignmentResponse::new)
                .toList();
    }

    // Get all assignments for a teacher
    @Transactional(readOnly = true)
    public List<TeacherCourseAssignmentResponse> getAssignmentsByTeacher(Long teacherId) {
        TeacherDetail teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + teacherId));

        return assignmentRepo.findByTeacher(teacher).stream()
                .map(TeacherCourseAssignmentResponse::new)
                .toList();
    }

    // Delete assignment
    public void removeAssignment(Long assignmentId) {
        if (!assignmentRepo.existsById(assignmentId)) {
            throw new IllegalArgumentException("Assignment not found with ID: " + assignmentId);
        }
        assignmentRepo.deleteById(assignmentId);
    }
}
