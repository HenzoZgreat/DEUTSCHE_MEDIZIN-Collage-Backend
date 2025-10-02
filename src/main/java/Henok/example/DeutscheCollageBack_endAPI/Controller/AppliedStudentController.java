package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AppliedStudentListResponseDTO;
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
     * Registers a new applicant with the provided details, optional document, and optional student photo.
     * Accessible to all (permitAll in SecurityConfig).
     * @param request The applicant details in DTO form.
     * @param document The uploaded document file (optional).
     * @param studentPhoto The uploaded student photo file (optional).
     * @return A response with the applicant ID and success message.
     * @throws IllegalArgumentException for invalid input.
     * @throws ResourceNotFoundException for missing referenced entities.
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerApplicant(
            @RequestPart(name = "data") AppliedStudentRegisterRequest request,
            @RequestPart(name = "document", required = false) MultipartFile document,
            @RequestPart(name = "studentPhoto", required = false) MultipartFile studentPhoto) {
        try {
            AppliedStudent applicant = appliedStudentService.registerApplicant(request, document, studentPhoto);
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
     * Retrieves all applicants with limited fields.
     * Restricted to REGISTRAR role.
     * @return A list of applicants as AppliedStudentListResponseDTOs.
     * @throws ResourceNotFoundException if no applicants are found.
     */
    @GetMapping
    public ResponseEntity<?> getAllApplicants() {
        try {
            List<AppliedStudentListResponseDTO> applicants = appliedStudentService.getAllApplicants();
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

    /**
     * Retrieves the student photo for an applicant.
     * Restricted to REGISTRAR role.
     * @param id The ID of the applicant.
     * @return The student photo as a byte array.
     * @throws ResourceNotFoundException if the applicant or photo is not found.
     */
    @GetMapping("/{id}/photo")
    public ResponseEntity<?> getStudentPhoto(@PathVariable Long id) {
        try {
            AppliedStudent applicant = appliedStudentService.getApplicantByIdForFile(id);
            if (applicant.getStudentPhoto() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("No student photo found for applicant with id: " + id));
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Adjust based on actual photo type if needed
                    .body(applicant.getStudentPhoto());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while retrieving student photo: " + e.getMessage()));
        }
    }

    /**
     * Retrieves the document for an applicant.
     * Restricted to REGISTRAR role.
     * @param id The ID of the applicant.
     * @return The document as a byte array.
     * @throws ResourceNotFoundException if the applicant or document is not found.
     */
    @GetMapping("/{id}/document")
    public ResponseEntity<?> getStudentDocument(@PathVariable Long id) {
        try {
            AppliedStudent applicant = appliedStudentService.getApplicantByIdForFile(id);
            if (applicant.getDocument() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("No document found for applicant with id: " + id));
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(applicant.getDocument());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while retrieving document: " + e.getMessage()));
        }
    }
}