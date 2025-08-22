package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCourseDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentCourse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-courses")
public class StudentCourseController {

    @Autowired
    private StudentCourseService studentCourseService;

    @PostMapping("/add")
    public ResponseEntity<?> addCourse(@RequestBody StudentCourseDTO dto) {
        try {
            studentCourseService.addCourse(dto);
            return ResponseEntity.ok("Course added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add course: " + e.getMessage()));
        }
    }

    @PutMapping("/score/{studentId}/{courseId}/{batchClassYearSemesterId}")
    public ResponseEntity<?> updateScore(@PathVariable Long studentId, @PathVariable Long courseId,
                                         @PathVariable Long batchClassYearSemesterId, @RequestBody StudentCourseDTO dto) {
        try {
            studentCourseService.updateScore(studentId, courseId, batchClassYearSemesterId, dto.getScore());
            return ResponseEntity.ok("Score updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update score: " + e.getMessage()));
        }
    }

    @PutMapping("/release/{studentId}/{courseId}/{batchClassYearSemesterId}")
    public ResponseEntity<?> releaseScore(@PathVariable Long studentId, @PathVariable Long courseId,
                                          @PathVariable Long batchClassYearSemesterId, @RequestBody StudentCourseDTO dto) {
        try {
            studentCourseService.releaseScore(studentId, courseId, batchClassYearSemesterId, dto.getIsReleased());
            return ResponseEntity.ok("Score release status updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update score release status: " + e.getMessage()));
        }
    }

    @GetMapping("/scores/{studentId}")
    public ResponseEntity<?> getStudentScores(@PathVariable Long studentId) {
        try {
            List<StudentCourse> scores = studentCourseService.getStudentScores(studentId);
            return ResponseEntity.ok(scores);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve scores: " + e.getMessage()));
        }
    }
}