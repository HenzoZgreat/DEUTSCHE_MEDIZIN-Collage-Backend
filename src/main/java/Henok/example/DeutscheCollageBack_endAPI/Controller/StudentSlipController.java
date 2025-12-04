package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips.StudentSlipBulkGenerationDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips.SingleStudentSlipGenerationDTO;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentCourseScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// New Controller: Handles student slip generation (enrollment of courses for semesters).
// Why: Separates concerns for bulk enrollments/slips from individual score management.
// Design: Reuses StudentCourseScoreService for business logic; no new service class created.
// Restricted to REGISTRAR role for security (add to SecurityConfig if needed).
@RestController
@RequestMapping("/api/student-slips")
public class StudentSlipController {

    @Autowired
    private StudentCourseScoreService studentCourseScoreService;

    /**
     * POST /api/student-slips/add-for-student
     *
     * Enrolls a single student in multiple courses for a specific BatchClassYearSemester (BCYS).
     * This generates a "slip" by creating multiple StudentCourseScore records with score=null and isReleased=false.
     *
     * Why: Allows registrars to prepare semester course lists (slips) for individual students at the start of the semester.
     * Validates prerequisites and prevents duplicates per existing service logic.
     *
     * Request Body: StudentSlipDTO (studentId, bcysId, sourceId, list of courseIds)
     *
     * @param dto The DTO containing student, BCYS, source, and list of courses to enroll.
     * @return Success message or error.
     */
    @PostMapping("/add-for-student")
    public ResponseEntity<?> addSlipForStudent(@RequestBody SingleStudentSlipGenerationDTO dto) {
        try {
            studentCourseScoreService.addCoursesForStudent(dto);
            return ResponseEntity.ok("Student slip generated successfully: Enrolled in " + dto.getCourseIds().size() + " courses");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to generate student slip: " + e.getMessage()));
        }
    }

    /**
     * POST /api/student-slips/add-for-multiple
     *
     * Bulk enrolls multiple students in their respective courses for a specific BCYS.
     * Each student can have a different list of courses, but same BCYS and source assumed for simplicity.
     * Creates multiple StudentCourseScore records per student.
     *
     * Why: Efficient for preparing slips for groups/classes at semester start; transactional for consistency.
     * Validates per student/course as in single add.
     *
     * Request Body: StudentSlipBulkDTO (bcysId, sourceId, list of {studentId, courseIds})
     *
     * @param bulkDto The bulk DTO for multiple students.
     * @return Success message with count or error.
     */
    @PostMapping("/add-for-multiple")
    public ResponseEntity<?> addSlipsForMultipleStudents(@RequestBody StudentSlipBulkGenerationDTO bulkDto) {
        try {
            int totalEnrollments = studentCourseScoreService.addCoursesForMultipleStudents(bulkDto);
            return ResponseEntity.ok("Bulk student slips generated successfully: " + totalEnrollments + " total enrollments created");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to generate bulk student slips: " + e.getMessage()));
        }
    }
}