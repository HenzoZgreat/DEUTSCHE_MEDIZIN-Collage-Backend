package Henok.example.DeutscheCollageBack_endAPI.migration.Controller;

import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import Henok.example.DeutscheCollageBack_endAPI.migration.DTO.BulkImportResponseDTO;
import Henok.example.DeutscheCollageBack_endAPI.migration.DTO.BulkImportResult;
import Henok.example.DeutscheCollageBack_endAPI.migration.DTO.CourseCreateDTO;
import Henok.example.DeutscheCollageBack_endAPI.migration.DTO.StudentImportDTO;
import Henok.example.DeutscheCollageBack_endAPI.migration.Service.BulkStudentImportService;
import Henok.example.DeutscheCollageBack_endAPI.migration.Service.CourseMigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Bulk Student Import Controller
@RestController
@RequestMapping("/api/migration")
@RequiredArgsConstructor
public class BulkImportController {

    private final BulkStudentImportService bulkStudentImportService;
    private final CourseMigrationService courseService;

    // -----------[Bulk Student Import]------------------
    // description - One-time endpoint for importing legacy students in bulk.
    //               Receives a list of StudentImportDTO (no files, no photos/documents).
    //               Skips invalid records, continues with valid ones.
    //               Secured for admin only (adjust @PreAuthorize as needed).
    // endpoint - POST /api/migration/students/bulk
    // body - List<StudentImportDTO> (JSON array)
    // success response - { "successCount": 150, "failedCount": 5, "message": "Bulk import completed" }
    // ErrorResponse - { "error": "description of error" }
    @PostMapping("/students/bulk")
    public ResponseEntity<Map<String, Object>> bulkImportStudents(@RequestBody List<StudentImportDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new BadRequestException("Request body cannot be empty");
        }

        BulkImportResult result = bulkStudentImportService.importStudents(dtos);

        Map<String, Object> response = new HashMap<>();
        response.put("successCount", result.getSuccessCount());
        response.put("failedCount", result.getFailedCount());
        response.put("failedUsernames", result.getFailedUsernames());
        response.put("message", "Bulk import completed");

        return ResponseEntity.ok(response);
    }

    /**
     * Bulk import endpoint.
     * On failure of individual records, they are skipped and listed in failedCourses.
     * Only ADMIN can perform this operation.
     */
    @PostMapping("/courses/bulk")
    public ResponseEntity<BulkImportResponseDTO> bulkImport(@RequestBody List<CourseCreateDTO> dtos) {
        BulkImportResponseDTO response = courseService.bulkImport(dtos);
        return ResponseEntity.ok(response);
    }
}
