package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy.StudentCopyBulkRequestDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy.StudentCopyDTO;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for student copy (transcript) operations.
 */
@RestController
@RequestMapping("/api/student-copy")
public class StudentCopyController {

    @Autowired
    private StudentCopyService studentCopyService;

    /**
     * Generates student copies for multiple students for the same classyear and semester.
     * 
     * @param request The request containing semesterId, classYearId, and list of studentIds
     * @return List of StudentCopyDTO containing all student information and course grades for each student
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateStudentCopies(@RequestBody StudentCopyBulkRequestDTO request) {
        try {
            // Validate request
            if (request == null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Request cannot be null"));
            }
            if (request.getSemesterId() == null || request.getSemesterId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Semester ID cannot be null or empty"));
            }
            if (request.getClassYearId() == null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("ClassYear ID cannot be null"));
            }
            if (request.getStudentIds() == null || request.getStudentIds().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Student IDs list cannot be null or empty"));
            }

            List<StudentCopyDTO> studentCopies = studentCopyService.generateStudentCopiesForMultipleStudents(
                    request.getStudentIds(),
                    request.getClassYearId(),
                    request.getSemesterId()
            );

            return ResponseEntity.ok(studentCopies);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to generate student copies: " + e.getMessage()));
        }
    }
}

