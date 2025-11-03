package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentScoreRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentAssessment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// StudentAssessmentController
@RestController
@RequestMapping("/api/student-assessments")
@RequiredArgsConstructor
public class StudentAssessmentController {

    private final StudentAssessmentService studentAssessmentService;

    // Records a student's score for a specific assessment
    // Why: Teacher enters score (e.g., 28/30 for MidTerm) — must be <= maxScore
    // Security: Only TEACHER (handled in SecurityConfig), and must own the assessment
    @PostMapping
    public ResponseEntity<?> recordStudentScore(
            @AuthenticationPrincipal User authenticatedUser,
            @RequestBody StudentScoreRequest request) {

        try {
            // Manual DTO validation
            if (request.getAssessmentId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Assessment ID is required"));
            }
            if (request.getStudentId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Student ID is required"));
            }
            if (request.getScore() == null || request.getScore() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Score must be non-negative"));
            }

            StudentAssessment saved = studentAssessmentService.recordScore(authenticatedUser, request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Score recorded successfully");
            response.put("studentId", saved.getStudent().getId());
            response.put("assessmentId", saved.getAssessment().getAssID());
            response.put("score", saved.getScore());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to record score: " + e.getMessage()));
        }
    }
}