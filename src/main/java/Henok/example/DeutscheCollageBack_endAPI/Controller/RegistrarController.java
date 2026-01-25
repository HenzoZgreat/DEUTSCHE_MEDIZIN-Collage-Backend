package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssessmentScoresResponse;
import Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarDashboardDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Assessment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.AssessmentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.RegistrarService;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarResponse;
import Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarUpdateRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.Students.StudentDetailsSummaryDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.RegistrarDetail;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

// RegistrarController
// Why: Handles HTTP requests for registrar endpoints, delegates to service, manages responses.
// Security: @PreAuthorize for role-based access.
// Error Handling: Catches exceptions, returns ErrorResponse with appropriate HTTP status.
@RestController
@RequestMapping("/api/registrar")
@RequiredArgsConstructor
public class RegistrarController {

    private final RegistrarService registrarService;
    private final StudentDetailService studentDetailsService;



    // Fetches dashboard data.
    // Why: Single endpoint for all dashboard info; returns 200 OK on success.
    // Error: 500 for internal errors with structured response.
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {
        try {
            RegistrarDashboardDTO data = registrarService.getDashboardData();
            return ResponseEntity.ok(data);
        } catch (ResourceNotFoundException ex) {
            // 404 - when required reference data is missing (e.g., ACTIVE status)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(ex.getMessage()));
        } catch (BadRequestException ex) {
            // 400 - for any client-side misuse (not used here yet, but ready)
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            // 500 - unexpected errors (DB failure, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to load dashboard data"));
        }
    }

    // Allows registrar to view all assessments that have been approved by department heads
    // Why: Registrars need to review and potentially release final scores after department head approval
    // Security: Only REGISTRAR role reaches here (handled in SecurityConfig)
    // Updated Controller method (replace or add alongside the previous one)
    @GetMapping("/head-approved-scores")
    public ResponseEntity<?> getHeadApprovedAssessmentScoresForRegistrar(@AuthenticationPrincipal User authenticatedUser) {

        try {
            List<AssessmentScoresResponse> responses = registrarService.getHeadApprovedAssessmentScoresForRegistrar(authenticatedUser);

            // Return empty list if none found
            return ResponseEntity.ok(responses.isEmpty() ? Collections.emptyList() : responses);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve head-approved assessments: " + e.getMessage()));
        }
    }

    // Add this method to RegistrarController
    @PutMapping("/assignments/{teacherCourseAssignmentId}/final-approve-all")
    public ResponseEntity<?> registrarFinalApproveOrRejectAll(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Long teacherCourseAssignmentId,
            @RequestParam AssessmentStatus status) {

        try {
            if (status != AssessmentStatus.ACCEPTED && status != AssessmentStatus.REJECTED) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Status must be ACCEPTED or REJECTED"));
            }

            List<Assessment> updated = registrarService.registrarApproveOrRejectAllAssessments(
                    authenticatedUser, teacherCourseAssignmentId, status);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "All assessments are " + status.name() + " by registrar");
            response.put("count", updated.size());
            response.put("registrarAction", status.name());

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process final approval: " + e.getMessage()));
        }
    }

    // GET /api/registrar/all - returns all userEnabled registrars (no password, no images)
    @GetMapping("/all")
    public ResponseEntity<?> getAllRegistrars() {
        try {
            List<RegistrarResponse> list = registrarService.getAllUserEnabledRegistrars();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve registrars: " + e.getMessage()));
        }
    }

    // GET /api/registrar/profile - get registrar profile by token
    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal User user) {
        try {
            RegistrarResponse resp = registrarService.getProfileByUser(user);
            return ResponseEntity.ok(resp);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to load profile: " + e.getMessage()));
        }
    }

    // GET /api/registrar/photo/{id}
    @GetMapping(value = "/photo/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getPhoto(@PathVariable Long id) {
        try {
            byte[] img = registrarService.getPhotographById(id);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(img);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to retrieve photo: " + e.getMessage()));
        }
    }

    // GET /api/registrar/nationalID/{id}
    @GetMapping(value = "/nationalID/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getNationalID(@PathVariable Long id) {
        try {
            byte[] img = registrarService.getNationalIdById(id);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(img);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to retrieve national ID: " + e.getMessage()));
        }
    }

    // PATCH /api/registrar/update - partial update using token
    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal User user,
            @RequestPart(name = "data") RegistrarUpdateRequest req,
            @RequestPart(name = "photograph", required = false) MultipartFile photograph,
            @RequestPart(name = "nationalIdImage", required = false) MultipartFile nationalIdImage) {
        try {
            RegistrarDetail updated = registrarService.updateProfileByUser(user, req, nationalIdImage, photograph);
            RegistrarResponse resp = registrarService.getProfileByUser(user);
            return ResponseEntity.ok(resp);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (BadRequestException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to update profile: " + e.getMessage()));
        }
    }

    // PATCH /api/registrar/update/{id} - partial update by id
    @PatchMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateById(
            @PathVariable Long id,
            @RequestPart(name = "data") RegistrarUpdateRequest req,
            @RequestPart(name = "photograph", required = false) MultipartFile photograph,
            @RequestPart(name = "nationalIdImage", required = false) MultipartFile nationalIdImage) {
        try {
            RegistrarDetail updated = registrarService.updateProfileById(id, req, nationalIdImage, photograph);
            RegistrarResponse resp = new RegistrarResponse();
            resp.setId(updated.getId());
            resp.setUsername(updated.getUser() != null ? updated.getUser().getUsername() : null);
            resp.setFirstNameAmharic(updated.getFirstNameAmharic());
            resp.setLastNameAmharic(updated.getLastNameAmharic());
            resp.setFirstNameEnglish(updated.getFirstNameEnglish());
            resp.setLastNameEnglish(updated.getLastNameEnglish());
            resp.setEmail(updated.getEmail());
            resp.setPhoneNumber(updated.getPhoneNumber());
            resp.setHasPhoto(updated.getPhotograph() != null && updated.getPhotograph().length > 0);
            resp.setHasNationalId(updated.getNationalIdImage() != null && updated.getNationalIdImage().length > 0);
            resp.setEnabled(updated.getUser() != null && updated.getUser().isEnabled());
            return ResponseEntity.ok(resp);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (BadRequestException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to update registrar: " + e.getMessage()));
        }
    }

    // DELETE /api/registrar/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegistrar(@PathVariable Long id) {
        try {
            registrarService.deleteRegistrarById(id);
            return ResponseEntity.ok(Map.of("message", "Registrar deleted"));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to delete registrar: " + e.getMessage()));
        }
    }

    /**
     * GET endpoint to retrieve all students with full details (summary view).
     * Protected endpoint – add proper role-based security as needed.
     */
    @GetMapping("/all-students")
    public ResponseEntity<?> getAllStudentsSummary() {
        try {
            List<StudentDetailsSummaryDTO> students = studentDetailsService.getAllStudentsSummary();
            return ResponseEntity.ok(students);
        } catch (ResourceNotFoundException e) {
            throw e; // Will be handled by global exception handler → 404 + ErrorResponse
        } catch (Exception e) {
            // Catch any unexpected error and wrap it gracefully
            throw new RuntimeException("Failed to retrieve students: " + e.getMessage(), e);
            // Your global handler will convert RuntimeException → 500 + ErrorResponse
        }
    }

    /**
     * Endpoint to initialize / recalculate and persist CGPA + total earned credit hours
     * for ALL students based on released grades.
     *
     * - Intended for one-time use or admin-triggered refresh (e.g. after grade publication).
     * - No request body required.
     * - Uses the applicable GradingSystem per department.
     * - Sets cgpa = 0.0 and totalEarnedCreditHours = 0 when no released grades exist.
     * - Protected endpoint → should be restricted to admin/registrar roles.
     */
    @PostMapping("/admin/reload-records-all")
    public ResponseEntity<Map<String, Object>> initializeCgpaForAllStudents() {
        try {
            Map<String, Object> result = studentDetailsService.calculateAndSaveCgpaForAllStudents();

            return ResponseEntity.ok(result);

        } catch (IllegalStateException e) {
            throw new BadRequestException("Cannot initialize CGPA: " + e.getMessage());

        } catch (Exception e) {
            // Catch unexpected runtime issues (db connection, etc.)
            throw new RuntimeException("Failed to initialize CGPA for all students: " + e.getMessage(), e);
        }
    }


}
