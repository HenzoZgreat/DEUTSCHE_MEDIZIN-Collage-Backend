package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AppliedStudentResponseDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.AppliedStudentRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.AppliedStudent;
import Henok.example.DeutscheCollageBack_endAPI.Enums.ApplicationStatus;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.AppliedStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applicants")
public class AppliedStudentController {

    @Autowired
    private AppliedStudentService appliedStudentService;

    /**
     * Registers a new applicant with the provided details and optional document.
     * Accessible to all (permitAll in SecurityConfig).
     * @param request The applicant details in DTO form.
     * @param document The uploaded document file (optional).
     * @return A response with the applicant ID and success message.
     * @throws IllegalArgumentException for invalid input.
     * @throws ResourceNotFoundException for missing referenced entities.
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerApplicant(
            @RequestPart(name = "data") AppliedStudentRegisterRequest request,
            @RequestPart(name = "document", required = false) MultipartFile document) {
        try {
            AppliedStudent applicant = appliedStudentService.registerApplicant(request, document);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Applicant registered successfully");
            response.put("applicantId", applicant.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while registering the applicant: " + e.getMessage()));
        }
    }

    /**
     * Updates the application status of an existing applicant.
     * Restricted to REGISTRAR role.
     * @param id The ID of the applicant.
     * @param statusRequest A map containing the new status.
     * @return A response with a success message.
     * @throws ResourceNotFoundException if the applicant is not found.
     * @throws IllegalArgumentException if the status is invalid.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long id, @RequestBody Map<String, String> statusRequest) {
        try {
            String statusStr = statusRequest.get("status");
            ApplicationStatus status = ApplicationStatus.valueOf(statusStr.toUpperCase());
            appliedStudentService.updateApplicationStatus(id, status);
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("message", "Applicant status updated successfully to " + status);
            }});
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while updating applicant status: " + e.getMessage()));
        }
    }

    /**
     * Retrieves all applicants.
     * Restricted to REGISTRAR role.
     * @return A list of all applicants as DTOs.
     * @throws ResourceNotFoundException if no applicants are found.
     */
    @GetMapping
    public ResponseEntity<?> getAllApplicants() {
        try {
            List<AppliedStudentResponseDTO> applicants = appliedStudentService.getAllApplicants();
            return ResponseEntity.ok(applicants);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while retrieving applicants: " + e.getMessage()));
        }
    }

    /**
     * Retrieves an applicant by ID.
     * Restricted to REGISTRAR role.
     * @param id The ID of the applicant.
     * @return The applicant details as a DTO.
     * @throws ResourceNotFoundException if the applicant is not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicantById(@PathVariable Long id) {
        try {
            AppliedStudentResponseDTO applicant = appliedStudentService.getApplicantById(id);
            return ResponseEntity.ok(applicant);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while retrieving applicant: " + e.getMessage()));
        }
    }
}