package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentStatusDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-statuses")
public class StudentStatusController {

    @Autowired
    private StudentStatusService studentStatusService;

    @PostMapping
    public ResponseEntity<?> addStudentStatuses(@RequestBody List<StudentStatusDTO> studentStatusDTOs) {
        try {
            studentStatusService.addStudentStatuses(studentStatusDTOs);
            return ResponseEntity.ok("Student statuses added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add student statuses: " + e.getMessage()));
        }
    }

    @PostMapping("/single")
    public ResponseEntity<?> addStudentStatus(@RequestBody StudentStatusDTO studentStatusDTO) {
        try {
            studentStatusService.addStudentStatus(studentStatusDTO);
            return ResponseEntity.ok("Student status added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add student status: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllStudentStatuses() {
        try {
            List<StudentStatus> studentStatuses = studentStatusService.getAllStudentStatuses();
            return ResponseEntity.ok(studentStatuses);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve student statuses: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentStatusById(@PathVariable Long id) {
        try {
            StudentStatus studentStatus = studentStatusService.getStudentStatusById(id);
            return ResponseEntity.ok(studentStatus);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve student status: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudentStatus(@PathVariable Long id, @RequestBody StudentStatusDTO studentStatusDTO) {
        try {
            studentStatusService.updateStudentStatus(id, studentStatusDTO);
            return ResponseEntity.ok("Student status updated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update student status: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudentStatus(@PathVariable Long id) {
        try {
            studentStatusService.deleteStudentStatus(id);
            return ResponseEntity.ok("Student status deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete student status: " + e.getMessage()));
        }
    }
}