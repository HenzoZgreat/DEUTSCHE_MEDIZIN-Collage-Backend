package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssessmentCreateRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.AssessmentScoresResponse;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores.BulkAssessmentRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores.BulkAssessmentUpdateRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Assessment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.AssessmentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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


    // Creates multiple assessments at once for a teacher's assigned course
    // Why: Allows bulk creation (e.g., Quiz1, Assignment1, Midterm in one request)
    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkAssessments(
            @AuthenticationPrincipal User authenticatedUser,
            @RequestBody BulkAssessmentRequest request) {

        try {
            List<Assessment> created = assessmentService.createBulkAssessments(authenticatedUser, request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Assessments created successfully");
            response.put("count", created.size());
            response.put("assessmentIds", created.stream().map(Assessment::getAssID).toList());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create assessments: " + e.getMessage()));
        }
    }

    // Updates multiple assessments at once
    // Why: Teacher can fix titles, max scores, due dates in bulk
    @PutMapping("/bulk")
    public ResponseEntity<?> updateBulkAssessments(
            @AuthenticationPrincipal User authenticatedUser,
            @RequestBody BulkAssessmentUpdateRequest request) {

        try {
            List<Assessment> updated = assessmentService.updateBulkAssessments(authenticatedUser, request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Assessments updated successfully");
            response.put("count", updated.size());

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update assessments: " + e.getMessage()));
        }
    }

    // Deletes multiple assessments by IDs
    // Why: Teacher can clean up wrong assessments in bulk
    @DeleteMapping("/bulk")
    public ResponseEntity<?> deleteBulkAssessments(
            @AuthenticationPrincipal User authenticatedUser,
            @RequestBody List<Long> assessmentIds) {

        try {
            if (assessmentIds == null || assessmentIds.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Assessment IDs list cannot be empty"));
            }

            assessmentService.deleteBulkAssessments(authenticatedUser, assessmentIds);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Assessments deleted successfully");
            response.put("count", assessmentIds.size());

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete assessments: " + e.getMessage()));
        }
    }

    // Gets all assessments for a specific teacher course assignment
    // Why: Teacher views all assessments (Quiz, Midterm, etc.) under one course + batch
    @GetMapping("/assignment/{teacherCourseAssignmentId}")
    public ResponseEntity<?> getAssessmentsByAssignment(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Long teacherCourseAssignmentId) {

        try {
            List<Assessment> assessments = assessmentService.getAssessmentsByAssignment(
                    authenticatedUser, teacherCourseAssignmentId);

            return ResponseEntity.ok(assessments);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve assessments: " + e.getMessage()));
        }
    }

    // Gets a single assessment by ID
    // Why: For detailed view or editing single assessment
    @GetMapping("/{assessmentId}")
    public ResponseEntity<?> getAssessmentById(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Long assessmentId) {

        try {
            Assessment assessment = assessmentService.getAssessmentById(authenticatedUser, assessmentId);

            return ResponseEntity.ok(assessment);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve assessment: " + e.getMessage()));
        }
    }

    /**
     * Approves or rejects all assessments for a teacher course assignment.
     * After approval, creates a notification for department heads.
     * 
     * @param authenticatedUser The authenticated teacher
     * @param teacherCourseAssignmentId The teacher course assignment ID
     * @param status The status (ACCEPTED or REJECTED)
     * @return Response with updated assessments
     */
    @PutMapping("/assignment/{teacherCourseAssignmentId}/approve")
    public ResponseEntity<?> approveOrRejectAssessmentsForAssignment(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Long teacherCourseAssignmentId,
            @RequestParam AssessmentStatus status) {

        try {
            // Validate status
            if (status != AssessmentStatus.ACCEPTED && status != AssessmentStatus.REJECTED) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Status must be ACCEPTED or REJECTED"));
            }

            List<Assessment> updated = assessmentService.approveOrRejectAssessmentsForAssignment(
                    authenticatedUser, teacherCourseAssignmentId, status);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Assessments " + status.name().toLowerCase() + " successfully");
            response.put("count", updated.size());
            response.put("status", status.name());
            response.put("assessmentIds", updated.stream().map(Assessment::getAssID).toList());

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update assessments: " + e.getMessage()));
        }
    }
}
