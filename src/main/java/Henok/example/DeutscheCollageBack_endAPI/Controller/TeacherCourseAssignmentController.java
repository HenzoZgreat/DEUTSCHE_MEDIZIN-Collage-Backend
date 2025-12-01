package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssignTeacherCoursesRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherCourseAssignmentResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
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
            if (requests == null || requests.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("At least one course assignment is required"));
            }
            List<TeacherCourseAssignmentResponse> result = assignmentService.assignCoursesToTeacher(teacherId, requests);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // GET: Get all assignments for teacher
    @GetMapping
    public ResponseEntity<List<TeacherCourseAssignmentResponse>> getAssignments(@PathVariable Long teacherId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByTeacher(teacherId));
    }

    // DELETE: Remove specific assignment
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> removeAssignment(
            @PathVariable Long teacherId,
            @PathVariable Long assignmentId) {
        assignmentService.removeAssignment(assignmentId);
        return ResponseEntity.noContent().build();
    }
}