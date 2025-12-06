package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips.*;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.SlipService;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentCourseScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// New Controller: Handles student slip pre-veiwing and generation (enrollment of courses for semesters).
// Why: Separates concerns for bulk enrollments/slips from individual score management.
// Design: Reuses StudentCourseScoreService for business logic; no new service class created.
// Restricted to REGISTRAR role for security (add to SecurityConfig if needed).
@RestController
@RequestMapping("/api/student-slips")
public class StudentSlipController {

    @Autowired
    private StudentCourseScoreService studentCourseScoreService;

    @Autowired
    private SlipService slipService;


    @PostMapping("/preview")
    public ResponseEntity<?> previewSlips(@RequestBody SlipPreviewRequest request) {

        try {
            List<StudentSlipPreviewDTO> previews = slipService.generateSlipPreviews(request);

            return ResponseEntity.ok(previews);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            // Log this in production: log.error("Unexpected error in slip preview", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate slip preview. Please try again later."));
        }
    }

    @PostMapping("/available-courses")
    public ResponseEntity<?> getAvailableCoursesForStudents(
            @RequestBody StudentIdsRequest request) {

        try {
            List<SlipCourseDTO> courses = slipService.getAvailableCoursesForStudents(request.getStudentIds());

            return ResponseEntity.ok(courses);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to load available courses"));
        }
    }


    /**
     * POST /api/student-slips/generate
     *
     * Generates course slips for one or many students in a specific BCYS.
     * All-or-nothing per student: if any course is already registered → skip entire student.
     * Continues with others. Returns detailed report.
     */
    @PostMapping("/generate")
    public ResponseEntity<StudentSlipGenerateResponseDTO> generateStudentSlips(
            @RequestBody StudentSlipGenerateRequestDTO request) {

        StudentSlipGenerateResponseDTO response =
                studentCourseScoreService.generateStudentSlips(request);

        return ResponseEntity.ok(response);
    }
}