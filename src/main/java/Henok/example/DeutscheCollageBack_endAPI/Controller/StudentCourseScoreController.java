package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.GradeDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCourseScore.StudentCourseScoreDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCourseScore.StudentCourseScoreResponseDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCourseScore.StudentCourseScoreBulkUpdateDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCourseScore.PaginatedResponseDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentCourseScore;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentCourseScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-course-scores")
public class StudentCourseScoreController {

    @Autowired
    private StudentCourseScoreService studentCourseScoreService;

    @PostMapping("/add")
    public ResponseEntity<?> addCourse(@RequestBody StudentCourseScoreDTO dto) {
        try {
            studentCourseScoreService.addCourse(dto);
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
                                         @PathVariable Long batchClassYearSemesterId, @RequestBody StudentCourseScoreDTO dto) {
        try {
            studentCourseScoreService.updateScore(studentId, courseId, batchClassYearSemesterId, dto.getScore());
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
                                          @PathVariable Long batchClassYearSemesterId, @RequestBody StudentCourseScoreDTO dto) {
        try {
            studentCourseScoreService.releaseScore(studentId, courseId, batchClassYearSemesterId, dto.getIsReleased());
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
            List<StudentCourseScore> scores = studentCourseScoreService.getStudentScores(studentId);
            return ResponseEntity.ok(scores);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve scores: " + e.getMessage()));
        }
    }

    @GetMapping("/{scoreId}/grade")
    public ResponseEntity<?> getGrade(@PathVariable Long scoreId) {
        try {
            GradeDTO grade = studentCourseScoreService.getGrade(scoreId);
            return ResponseEntity.ok(grade);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * GET /api/student-course-scores/all
     *
     * Returns paginated list of ALL student course scores with rich details.
     * Supports multiple optional filters (all combinable).
     *
     * Security: Should be restricted to REGISTRAR / ADMIN roles via @PreAuthorize
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllStudentCourseScores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,

            // Optional filters (all nullable by default)
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long bcysId,
            @RequestParam(required = false) Long studentId,

            // Optional: filter only released / unreleased scores
            @RequestParam(required = false) Boolean isReleased) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);

            PaginatedResponseDTO<StudentCourseScoreResponseDTO> response =
                    studentCourseScoreService.getAllStudentCourseScoresPaginated(
                            pageable, courseId, bcysId, studentId, isReleased);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve filtered student course scores: " + e.getMessage()));
        }
    }

    @PutMapping("/bulk-update")
    public ResponseEntity<?> bulkUpdateStudentCourseScores(@RequestBody StudentCourseScoreBulkUpdateDTO bulkUpdateDTO) {
        try {
            studentCourseScoreService.bulkUpdateStudentCourseScores(bulkUpdateDTO);
            return ResponseEntity.ok("Bulk update completed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to perform bulk update: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/student-course-scores/bulk-delete
     *
     * Deletes multiple student course score records by their IDs.
     *
     * - Only accessible to REGISTRAR or ADMIN roles (add @PreAuthorize in production)
     * - Returns success message with count of deleted records
     * - Handles partial failures gracefully (continues deleting valid IDs even if some fail)
     * - Returns structured error if nothing could be deleted or major issue occurs
     */
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<?> bulkDeleteStudentCourseScores(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("No IDs provided for deletion"));
            }

            int deletedCount = studentCourseScoreService.bulkDeleteByIds(ids);

            if (deletedCount == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("None of the provided IDs were found"));
            }

            return ResponseEntity.ok("Successfully deleted " + deletedCount + " record(s)");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to perform bulk deletion: " + e.getMessage()));
        }
    }

}