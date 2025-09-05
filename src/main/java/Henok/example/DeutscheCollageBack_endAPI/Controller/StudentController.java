package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentUpdateDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentDetails;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentDetailService studentDetailsService;


    // Retrieves all active students
    // Why: For admin/registrar to view all active student records
    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        try {
            List<StudentDetails> students = studentDetailsService.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve students: " + e.getMessage()));
        }
    }

    // Retrieves a student by ID
    // Why: For detailed view of a specific student
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        try {
            StudentDetails student = studentDetailsService.getStudentById(id);
            return ResponseEntity.ok(student);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve student: " + e.getMessage()));
        }
    }

    // Updates a student's details with optional file uploads
    // Why: Allows modification of student information and files
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateStudent(
            @PathVariable Long id,
            @RequestPart(name = "data") StudentUpdateDTO dto,
            @RequestPart(name = "studentPhoto", required = false) MultipartFile studentPhoto,
            @RequestPart(name = "document", required = false) MultipartFile document) {
        try {
            StudentDetails updated = studentDetailsService.updateStudent(id, dto, studentPhoto, document);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Student updated successfully");
            response.put("studentId", updated.getId().toString());
            response.put("userId", updated.getUser().getId().toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Student update failed due to duplicate entry: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update student: " + e.getMessage()));
        }
    }

    // Enables a student account
    // Why: Activates student login by enabling user account
    @PostMapping("/{id}/enable")
    public ResponseEntity<?> enableStudent(@PathVariable Long id) {
        try {
            studentDetailsService.enableStudent(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Student enabled successfully");
            response.put("studentId", id.toString());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to enable student: " + e.getMessage()));
        }
    }

    // Disables a student account
    // Why: Suspends student login without deleting records
    @PostMapping("/{id}/disable")
    public ResponseEntity<?> disableStudent(@PathVariable Long id) {
        try {
            studentDetailsService.disableStudent(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Student disabled successfully");
            response.put("studentId", id.toString());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to disable student: " + e.getMessage()));
        }
    }
}