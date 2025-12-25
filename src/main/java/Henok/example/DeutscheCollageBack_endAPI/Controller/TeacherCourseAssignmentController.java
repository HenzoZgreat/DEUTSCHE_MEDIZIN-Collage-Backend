package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssignTeacherCoursesRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherCourseAssignmentResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.TeacherCourseAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/teachers/{teacherId}/course-assignments")
@RequiredArgsConstructor
public class TeacherCourseAssignmentController {

    private final TeacherCourseAssignmentService assignmentService;

    // POST: Assign multiple courses
    @PostMapping
    public ResponseEntity<?> assignCourses(
            @PathVariable Long teacherId,
            @RequestBody List<AssignTeacherCoursesRequest> requests) {

        try {
            List<TeacherCourseAssignmentResponse> result =
                    assignmentService.assignCoursesToTeacher(teacherId, requests);

            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (IllegalArgumentException e) {
            // Covers all validation errors: missing data, not found, duplicates
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            // Handles unexpected errors (DB constraints, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to assign courses: " + e.getMessage()));
        }
    }

    // GET: Get all assignments for teacher
    @GetMapping
    public ResponseEntity<List<TeacherCourseAssignmentResponse>> getAssignments(@PathVariable Long teacherId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByTeacher(teacherId));
    }

    /**
     * Deletes a specific course assignment for a teacher.
     * Also removes all related assessments and student assessment records.
     */
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<?> removeAssignment(
            @PathVariable Long teacherId,
            @PathVariable Long assignmentId) {

        try {
            assignmentService.removeAssignment(assignmentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            // Covers "Assignment not found" and validation errors from AssessmentService
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            // Assessments not found (from AssessmentService)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            // Any unexpected error (DB constraint, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete assignment: " + e.getMessage()));
        }
    }
}