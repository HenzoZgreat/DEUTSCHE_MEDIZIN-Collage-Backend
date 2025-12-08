package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy.StudentCopyDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy.StudentCopyRequestDTO;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for student copy (transcript) operations.
 */
@RestController
@RequestMapping("/api/student-copy")
public class StudentCopyController {

    @Autowired
    private StudentCopyService studentCopyService;

    /**
     * Generates a student copy (transcript) for a specific classyear and semester.
     * 
     * @param request The request containing studentId, classYearId, and semesterId
     * @return StudentCopyDTO containing all student information and course grades
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateStudentCopy(@RequestBody StudentCopyRequestDTO request) {
        try {
            StudentCopyDTO studentCopy = studentCopyService.generateStudentCopy(request);
            return ResponseEntity.ok(studentCopy);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to generate student copy: " + e.getMessage()));
        }
    }
}

