package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssignTeacherCoursesRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherCourseAssignmentResponse;
import Henok.example.DeutscheCollageBack_endAPI.Entity.BatchClassYearSemester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Course;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherCourseAssignment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherDetail;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
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
    private final AssessmentService assessmentService;
    private final AssessmentRepo assessmentRepository;
    private final CourseRepo courseRepo;
    private final BatchClassYearSemesterRepo bcysRepo;

    private final EntityManager em;

    /**
     * Assigns multiple courses to a teacher in one transaction.
     * Validates teacher, courses, BCYS, and prevents duplicates.
     * All errors are thrown as IllegalArgumentException with clear messages.
     */
    public List<TeacherCourseAssignmentResponse> assignCoursesToTeacher(
            Long teacherId,
            List<AssignTeacherCoursesRequest> requests) {

        // Input validation
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("At least one course assignment is required");
        }

        // Validate teacher exists
        TeacherDetail teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + teacherId));

        List<TeacherCourseAssignment> assignments = new ArrayList<>();

        for (AssignTeacherCoursesRequest req : requests) {
            // Validate course exists
            Course course = courseRepo.findById(req.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + req.getCourseId()));

            // Validate BCYS exists
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

        // Persist all at once
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

    /**
     * Deletes a TeacherCourseAssignment by ID.
     * Before deletion:
     *   1. Finds all Assessments linked to this assignment (via whatever FK you have – adjust query if needed)
     *   2. Calls AssessmentService to safely delete all related assessments + student assessments
     *   3. Finally deletes the assignment itself.
     *
     * All operations are inside one @Transactional – rollback on any failure.
     */
    public void removeAssignment(Long assignmentId) {
        // 1. Check if assignment exists
        TeacherCourseAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found with ID: " + assignmentId));

        // 2. Find all assessment IDs linked to this assignment
        // Adjust this query according to your actual relationship.
        // Example assuming Assessment has a field assignmentId (or a ManyToOne to TeacherCourseAssignment)
        List<Long> assessmentIds = assessmentRepository.findAssessmentIdsByAssignmentId(assignmentId);

        // 3. Delete assessments cascade (including StudentAssessments)
        if (!assessmentIds.isEmpty()) {
            assessmentService.deleteAssessmentsByIds(assessmentIds);
        }

        // 4. Delete the assignment itself
        assignmentRepo.delete(assignment);
    }
}
