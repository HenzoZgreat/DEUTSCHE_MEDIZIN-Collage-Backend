package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssessmentCreateRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Assessment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherCourseAssignment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.AssessmentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.AssessmentRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.TeacherCourseAssignmentRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// AssessmentService
@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final TeacherCourseAssignmentRepository tcaRepository;
    private final AssessmentRepo assessmentRepository;
    private final TeacherRepository teacherDetailRepository;

    // Creates assessment under teacher's assigned course + BCYS
    // Why: Only the teacher who is assigned (via TeacherCourseAssignment) can create
    @Transactional
    public Assessment createAssessment(User authenticatedUser, AssessmentCreateRequest request) {

        // Step 1: Get TeacherDetail from authenticated user
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        // Step 2: Validate TeacherCourseAssignment exists and belongs to this teacher
        TeacherCourseAssignment tca = tcaRepository.findById(request.getTeacherCourseAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned course not found"));

        if (!tca.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You are not assigned to this course or batch");
        }

        // Step 3: Create and save Assessment
        Assessment assessment = new Assessment();
        assessment.setTeacherCourseAssignment(tca);
        assessment.setAssTitle(request.getAssTitle().trim());
        assessment.setMaxScore(request.getMaxScore());
        assessment.setDueDate(request.getDueDate());
        assessment.setDescription(request.getDescription());
        assessment.setAssStatus(AssessmentStatus.PENDING);
        assessment.setCreatedAt(LocalDateTime.now());

        return assessmentRepository.save(assessment);
    }
}
