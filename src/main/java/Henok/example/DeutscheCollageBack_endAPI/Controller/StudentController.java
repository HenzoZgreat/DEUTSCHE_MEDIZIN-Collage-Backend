package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.Student.StudentProfileResponse;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips.StudentsListForSlipDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.Students.StudentDetailsDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.Students.StudentUpdateDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.Students.StudentListDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentDetailService;
import Henok.example.DeutscheCollageBack_endAPI.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @Autowired
    private UserService userService;


    // Retrieves all active students as DTOs
    // Why: For admin/registrar to view all active student records as DTOs
    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        try {
            List<StudentListDTO> students = studentDetailsService.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve students: " + e.getMessage()));
        }
    }

    // Retrieves a student by ID as a DTO
    // Why: For detailed view of a specific student
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        try {
            StudentDetailsDTO student = studentDetailsService.getStudentById(id);
            return ResponseEntity.ok(student);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve student: " + e.getMessage()));
        }
    }

    // ------------[Get Authenticated Student's Full Profile] --------
    @GetMapping("/profile")
    public ResponseEntity<?> getMyStudentProfile() {
        try {
            // Extract username from JWT token (authenticated user)
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // Load the User entity first
            User user = (User) userService.loadUserByUsername(username);

            // Ensure the user has STUDENT role - extra safety layer
            if (user.getRole() != Role.STUDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "Access denied",
                                "message", "This endpoint is only for students"
                        ));
            }

            // Delegate to service to build full student profile DTO
            StudentProfileResponse response = studentDetailsService.getStudentProfileByUser(user);

            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Student profile not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to load student profile"));
        }
    }

    // Get All Students for Slip Production
    @GetMapping("/slip-production")
    public ResponseEntity<List<StudentsListForSlipDTO>> getStudentsForSlipProduction() {
        List<StudentsListForSlipDTO> students = studentDetailsService.getStudentsForSlipProduction();
        return ResponseEntity.ok(students);
    }

    // Updates a student's details with optional file uploads and returns DTO
    // Why: Allows modification of student information and files, returns updated DTO
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateStudent(
            @PathVariable Long id,
            @RequestPart(name = "data") StudentUpdateDTO dto,
            @RequestPart(name = "studentPhoto", required = false) MultipartFile studentPhoto,
            @RequestPart(name = "document", required = false) MultipartFile document) {
        try {
            StudentDetailsDTO updated = studentDetailsService.updateStudent(id, dto, studentPhoto, document);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Student updated successfully");
            response.put("studentId", updated.getId().toString());
            response.put("userId", updated.getUserId().toString());
            response.put("student", updated);
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