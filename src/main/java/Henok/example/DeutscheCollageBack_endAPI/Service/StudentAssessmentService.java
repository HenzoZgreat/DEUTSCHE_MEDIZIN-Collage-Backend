package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentScoreRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// StudentAssessmentService
@Service
@RequiredArgsConstructor
public class StudentAssessmentService {

    private final AssessmentRepo assessmentRepository;
    private final StudentDetailsRepository studentDetailsRepository;
    private final StudentAssessmentRepo studentAssessmentRepository;
    private final TeacherRepository teacherDetailRepository;

    // Records a single student score for an assessment
    // Why: Ensures score ≤ maxScore and teacher owns the assessment
    @Transactional
    public StudentAssessment recordScore(User authenticatedUser, StudentScoreRequest request) {

        // Step 1: Get authenticated teacher
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        // Step 2: Load Assessment and validate ownership
        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if (!assessment.getTeacherCourseAssignment().getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You are not authorized to grade this assessment");
        }

        // Step 3: Validate score ≤ maxScore
        if (request.getScore() > assessment.getMaxScore()) {
            throw new IllegalArgumentException(
                    "Score (" + request.getScore() + ") exceeds max score (" + assessment.getMaxScore() + ")"
            );
        }

        // Step 4: Load Student
        StudentDetails student = studentDetailsRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Step 5: Prevent duplicate score
        StudentAssessmentKey key = new StudentAssessmentKey(student.getId(), assessment.getAssID());
        if (studentAssessmentRepository.existsById(key)) {
            throw new IllegalArgumentException("Score already recorded for this student and assessment");
        }

        // Step 6: Save score
        StudentAssessment sa = new StudentAssessment();
        sa.setId(key);
        sa.setStudent(student);
        sa.setAssessment(assessment);
        sa.setScore(request.getScore());
        sa.setGradedAt(LocalDateTime.now());

        return studentAssessmentRepository.save(sa);
    }
}
