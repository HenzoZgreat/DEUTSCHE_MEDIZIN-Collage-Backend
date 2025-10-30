package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssessmentCreateRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Assessment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.AssessmentService;
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

// AssessmentController
@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    // Creates a new assessment for the authenticated teacher
    // Why: Teacher defines assessment (e.g., MidTerm, max 30) for their assigned course + BCYS
    // Security: JWT + role check handled in SecurityConfig → only TEACHER role reaches here
    @PostMapping
    public ResponseEntity<?> createAssessment(
            @AuthenticationPrincipal User authenticatedUser,
            @RequestBody AssessmentCreateRequest request) {

        try {
            // Basic DTO validation
            if (request.getTeacherCourseAssignmentId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Teacher course assignment ID is required"));
            }
            if (request.getAssTitle() == null || request.getAssTitle().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Assessment title is required"));
            }
            if (request.getMaxScore() == null || request.getMaxScore() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Max score must be a positive number"));
            }
            if (request.getAssTitle().length() > 150) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Title cannot exceed 150 characters"));
            }

            Assessment assessment = assessmentService.createAssessment(authenticatedUser, request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Assessment created successfully");
            response.put("assessmentId", assessment.getAssID());
            response.put("title", assessment.getAssTitle());
            response.put("maxScore", assessment.getMaxScore());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to create assessment: " + e.getMessage()));
        }
    }
}
