package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssessmentScoresResponse;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentScoreRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores.BulkStudentScoreRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores.BulkScoreRecordResponse;
import Henok.example.DeutscheCollageBack_endAPI.DTO.UpdateScoreRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentAssessment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    // Retrieves all assessments and student scores for a specific course assignment
    // Why: Teachers need a complete view (grid-like) of all their assessments and every student's score
    // Path variable: teacherCourseAssignmentId – identifies the exact course + batch the teacher teaches
    // Security: Only TEACHER role (handled in SecurityConfig), ownership validated in service
    @GetMapping("/courses/{teacherCourseAssignmentId}/scores")
    public ResponseEntity<?> getAssessmentScores(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Long teacherCourseAssignmentId) {

        try {
            AssessmentScoresResponse response = studentAssessmentService.getScores(authenticatedUser, teacherCourseAssignmentId);

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve scores: " + e.getMessage()));
        }
    }

    // Updates an existing student score for an assessment
    // Why: Teachers need to correct mistakes or update scores after review
    // Security: Only the teacher who owns the assessment can update
    @PutMapping("/{assessmentId}/{studentId}")
    public ResponseEntity<?> updateStudentScore(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Long assessmentId,
            @PathVariable Long studentId,
            @RequestBody UpdateScoreRequest request) {

        try {
            // Basic validation
            if (request.getScore() == null || request.getScore() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Score must be non-negative"));
            }

            StudentAssessment updated = studentAssessmentService.updateScore(
                    authenticatedUser, assessmentId, studentId, request.getScore());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Score updated successfully");
            response.put("studentId", updated.getStudent().getId());
            response.put("assessmentId", updated.getAssessment().getAssID());
            response.put("newScore", updated.getScore());
            response.put("updatedAt", updated.getGradedAt());
            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update score: " + e.getMessage()));
        }
    }


    // Bulk record (create new scores)
    @PostMapping("/bulk")
    public ResponseEntity<?> recordBulkStudentScores(
            @AuthenticationPrincipal User authenticatedUser,
            @RequestBody BulkStudentScoreRequest request) {

        try {
            if (request.getScores() == null || request.getScores().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Scores list cannot be empty"));
            }

            BulkScoreRecordResponse response = studentAssessmentService.recordBulkScores(
                    authenticatedUser, request.getScores());

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Scores recorded successfully");
            result.put("recordedCount", response.getRecordedCount());
            result.put("failedRecords", response.getFailedRecords());

            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(
                            e instanceof ResourceNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to record scores: " + e.getMessage()));
        }
    }

    // Bulk update (update existing or create if missing)
    @PutMapping("/bulk")
    public ResponseEntity<?> updateBulkStudentScores(
            @AuthenticationPrincipal User authenticatedUser,
            @RequestBody BulkStudentScoreRequest request) {

        try {
            if (request.getScores() == null || request.getScores().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Scores list cannot be empty"));
            }

            BulkScoreRecordResponse response = studentAssessmentService.updateBulkScores(
                    authenticatedUser, request.getScores());

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Scores updated successfully");
            result.put("updatedCount", response.getRecordedCount());
            result.put("failedUpdates", response.getFailedRecords());

            return ResponseEntity.ok(result);

        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(
                            e instanceof ResourceNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update scores: " + e.getMessage()));
        }
    }
}