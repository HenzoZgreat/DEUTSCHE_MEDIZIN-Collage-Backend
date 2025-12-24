package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssessmentCreateRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.AssessmentScoresResponse;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores.BulkAssessmentRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores.BulkAssessmentUpdateRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Enums.AssessmentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import Henok.example.DeutscheCollageBack_endAPI.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// AssessmentService
@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final TeacherCourseAssignmentRepository tcaRepository;
    private final AssessmentRepo assessmentRepository;
    private final TeacherRepository teacherDetailRepository;
    private final NotificationService notificationService;


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

// Add these methods to your TeacherService (or AssessmentService if you prefer separate)

    @Transactional
    public List<Assessment> createBulkAssessments(User authenticatedUser, BulkAssessmentRequest request) {
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        TeacherCourseAssignment tca = tcaRepository.findById(request.getTeacherCourseAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Course assignment not found"));

        if (!tca.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You are not assigned to this course");
        }

        List<Assessment> created = new ArrayList<>();
        for (BulkAssessmentRequest.SingleAssessmentCreate dto : request.getAssessments()) {
            if (dto.getAssTitle() == null || dto.getAssTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Assessment title is required");
            }
            if (dto.getMaxScore() == null || dto.getMaxScore() <= 0) {
                throw new IllegalArgumentException("Max score must be positive");
            }

            Assessment assessment = new Assessment();
            assessment.setTeacherCourseAssignment(tca);
            assessment.setAssTitle(dto.getAssTitle().trim());
            assessment.setMaxScore(dto.getMaxScore());
            assessment.setDueDate(dto.getDueDate());
            assessment.setDescription(dto.getDescription());
            assessment.setAssStatus(AssessmentStatus.PENDING);
            assessment.setCreatedAt(LocalDateTime.now());

            created.add(assessmentRepository.save(assessment));
        }

        return created;
    }

    @Transactional
    public List<Assessment> updateBulkAssessments(User authenticatedUser, BulkAssessmentUpdateRequest request) {
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        List<Assessment> updated = new ArrayList<>();
        for (BulkAssessmentUpdateRequest.SingleAssessmentUpdate dto : request.getAssessments()) {
            Assessment assessment = assessmentRepository.findById(dto.getAssessmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + dto.getAssessmentId()));

            if (!assessment.getTeacherCourseAssignment().getTeacher().getId().equals(teacher.getId())) {
                throw new IllegalArgumentException("Not authorized to update assessment: " + dto.getAssessmentId());
            }

            if (dto.getAssTitle() != null && !dto.getAssTitle().trim().isEmpty()) {
                assessment.setAssTitle(dto.getAssTitle().trim());
            }
            if (dto.getMaxScore() != null) {
                if (dto.getMaxScore() <= 0) {
                    throw new IllegalArgumentException("Max score must be positive for assessment: " + dto.getAssessmentId());
                }
                assessment.setMaxScore(dto.getMaxScore());
            }
            if (dto.getDueDate() != null) {
                assessment.setDueDate(dto.getDueDate());
            }
            if (dto.getDescription() != null) {
                assessment.setDescription(dto.getDescription());
            }

            updated.add(assessmentRepository.save(assessment));
        }

        return updated;
    }

    // Update the deleteBulkAssessments method in TeacherService
    @Transactional
    public void deleteBulkAssessments(User authenticatedUser, List<Long> assessmentIds) {
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        // First: validate all assessments belong to the teacher
        for (Long id : assessmentIds) {
            Assessment assessment = assessmentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + id));

            if (!assessment.getTeacherCourseAssignment().getTeacher().getId().equals(teacher.getId())) {
                throw new IllegalArgumentException("Not authorized to delete assessment: " + id);
            }
        }

        // Second: delete all related StudentAssessment records first
        // Why: StudentAssessment has composite FK to Assessment → must delete children before parent
        assessmentRepository.deleteStudentAssessmentsByAssessmentIds(assessmentIds);

        // Third: delete the assessments themselves
        assessmentRepository.deleteAllById(assessmentIds);
    }

    // Optional: if you want a single delete method
    @Transactional
    public void deleteAssessment(User authenticatedUser, Long assessmentId) {
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if (!assessment.getTeacherCourseAssignment().getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("Not authorized to delete this assessment");
        }

        // Delete child scores first
        assessmentRepository.deleteStudentAssessmentsByAssessmentId(assessmentId);

        // Then delete the assessment
        assessmentRepository.delete(assessment);
    }

    @Transactional(readOnly = true)
    public List<Assessment> getAssessmentsByAssignment(User authenticatedUser, Long teacherCourseAssignmentId) {
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        TeacherCourseAssignment tca = tcaRepository.findById(teacherCourseAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Course assignment not found"));

        if (!tca.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You are not assigned to this course");
        }

        return assessmentRepository.findByTeacherCourseAssignmentOrderByCreatedAtAsc(tca);
    }

    @Transactional(readOnly = true)
    public Assessment getAssessmentById(User authenticatedUser, Long assessmentId) {
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if (!assessment.getTeacherCourseAssignment().getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("Not authorized to view this assessment");
        }

        return assessment;
    }

    /**
     * Approves or rejects all assessments for a teacher course assignment.
     * After approval, creates a notification for department heads.
     * 
     * @param authenticatedUser The authenticated teacher
     * @param teacherCourseAssignmentId The teacher course assignment ID
     * @param status The status to set (ACCEPTED or REJECTED)
     * @return List of updated assessments
     */
    @Transactional
    public List<Assessment> approveOrRejectAssessmentsForAssignment(
            User authenticatedUser, 
            Long teacherCourseAssignmentId, 
            AssessmentStatus status) {
        
        // Step 1: Get authenticated teacher
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));
        
        // Step 2: Load TeacherCourseAssignment and validate ownership
        TeacherCourseAssignment tca = tcaRepository.findById(teacherCourseAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Course assignment not found"));
        
        if (!tca.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You are not assigned to this course or batch");
        }
        
        // Step 3: Get all assessments for this assignment
        List<Assessment> assessments = assessmentRepository.findByTeacherCourseAssignmentOrderByCreatedAtAsc(tca);
        
        if (assessments.isEmpty()) {
            throw new IllegalArgumentException("No assessments found for this course assignment");
        }
        
        // Step 4: Update all assessments' assStatus
        for (Assessment assessment : assessments) {
            assessment.setAssStatus(status);
            assessment.setHeadApproval(AssessmentStatus.PENDING);
            assessmentRepository.save(assessment);
        }
        
        // Step 5: If approved, create notification for department heads
        if (status == AssessmentStatus.ACCEPTED) {
            String courseCode = tca.getCourse().getCCode();
            String courseTitle = tca.getCourse().getCTitle();
            String bcysName = tca.getBcys().getDisplayName();
            String message = String.format(
                "Teacher %s has approved assessments for course %s (%s) - %s. Please review.",
                teacher.getUser().getUsername(),
                courseCode,
                courseTitle,
                bcysName
            );
            
            notificationService.createNotification(
                List.of(Role.DEPARTMENT_HEAD),
                null,
                Role.TEACHER,
                message
            );
        }
        
        return assessments;
    }
}

