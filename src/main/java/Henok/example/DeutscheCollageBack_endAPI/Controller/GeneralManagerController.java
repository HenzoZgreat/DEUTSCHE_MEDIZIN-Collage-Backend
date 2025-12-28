package Henok.example.DeutscheCollageBack_endAPI.Controller;

// GeneralManagerController – REST endpoints for general manager profile management.
// Protected by role GENERAL_MANAGER.
// Uses @AuthenticationPrincipal to get the authenticated user from JWT.
// Delegates all business logic to GeneralManagerService.
// Errors are handled globally via @ControllerAdvice (assumed to exist).

import Henok.example.DeutscheCollageBack_endAPI.DTO.GeneralManager.GeneralManagerDashboardDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.GeneralManager.GeneralManagerDetailDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.GeneralManager.GeneralManagerUpdateDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Service.GeneralManagerService;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentDetailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;

import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/general-managers")
@PreAuthorize("hasRole('GENERAL_MANAGER')")
public class GeneralManagerController {

    private final GeneralManagerService generalManagerService;
    private final StudentDetailService studentDetailService;

    public GeneralManagerController(GeneralManagerService generalManagerService, StudentDetailService studentDetailService) {
        this.generalManagerService = generalManagerService;
        this.studentDetailService = studentDetailService;
    }

    // GET /api/general-managers/profile
    // Returns the profile of the authenticated general manager.
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal User user) {
        try {
            GeneralManagerDetailDTO profile = generalManagerService.getProfileByUser(user);
            return ResponseEntity.ok(profile);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to load profile: " + e.getMessage()));
        }
    }

    // PATCH /api/general-managers/update
    // Partial update – only provided fields are modified.
    @PatchMapping("/update")
        public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid GeneralManagerUpdateDTO updateDTO) {
        try {
            GeneralManagerDetailDTO updated = generalManagerService.updateProfile(user, updateDTO);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
        } catch (BadRequestException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Data integrity violation: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    // GET /api/general-managers/get-all-students-cgpa
    @GetMapping("/get-all-students-cgpa")
    public ResponseEntity<?> getAllStudentsWithCGPA() {
        try {
            return ResponseEntity.ok(studentDetailService.getAllStudentsWithCGPA());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Data integrity violation: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve CGPA list: " + e.getMessage()));
        }
    }

    // GET /api/general-managers/dashboard
    // Returns comprehensive dashboard data for the General Manager
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        try {
            GeneralManagerDashboardDTO dashboard = generalManagerService.getDashboardData();
            return ResponseEntity.ok(dashboard);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to load dashboard: " + e.getMessage()));
        }
    }
}