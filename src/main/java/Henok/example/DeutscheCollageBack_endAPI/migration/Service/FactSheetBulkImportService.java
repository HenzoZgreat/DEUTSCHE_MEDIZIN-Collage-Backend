package Henok.example.DeutscheCollageBack_endAPI.migration.Service;

// Updated FactSheetBulkImportService

import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import Henok.example.DeutscheCollageBack_endAPI.migration.DTO.BulkImportResult;
import Henok.example.DeutscheCollageBack_endAPI.migration.DTO.StudentCourseScoreImportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FactSheetBulkImportService {

    private final UserRepository userRepository;
    private final CourseRepo courseRepository;
    private final BatchClassYearSemesterRepo batchRepository;
    private final CourseSourceRepo courseSourceRepository;
    private final StudentCourseScoreRepo scoreRepository;

    /**
 * Bulk imports student course scores from a list of DTOs where all values come as strings.
 * 
 * Key behaviors:
 * - Parses string IDs and score safely – invalid formats are treated as failures.
 * - Sets isReleased = true if score is present (non-null/non-empty), false if score is null/empty/zero-string.
 * - If "is_released" is explicitly "1" → true, "0" → false (overrides the score-based rule).
 * - Skips any record that fails validation or already exists, continues with the rest.
 * - Logs every step with System.out.println (as requested for this one-time massive import).
 * - Logs detailed failure blocks exactly in the required format.
 * - Returns success count and a list of short failure identifiers for the API response.
 */
public BulkImportResult bulkImportScores(List<StudentCourseScoreImportDTO> dtoList) {
    List<String> failedRecords = new ArrayList<>();
    int successfulCount = 0;

    System.out.println("=== STARTING BULK IMPORT OF " + dtoList.size() + " RECORDS ===");

    for (int i = 0; i < dtoList.size(); i++) {
        StudentCourseScoreImportDTO dto = dtoList.get(i);

        // Use raw string values for logging even if parsing fails later
        String rawStudentId = dto.getStudent_user_id() != null ? dto.getStudent_user_id().trim() : "";
        String rawCourseId = dto.getCourse_id() != null ? dto.getCourse_id().trim() : "";
        String rawBatchId = dto.getBatch_class_year_semester_id() != null ? dto.getBatch_class_year_semester_id().trim() : "";

        String recordKey = rawStudentId + " - " + rawCourseId + " - " + rawBatchId;

        try {
            // Parse required IDs – throw if empty or not numeric
            Long studentId = parseLong(rawStudentId, "student_user_id");
            Long courseId = parseLong(rawCourseId, "course_id");
            Long batchId = parseLong(rawBatchId, "batch_class_year_semester_id");
            Long sourceId = parseLong(dto.getSource_id(), "source_id");

            // Update recordKey with parsed longs (cleaner for response)
            recordKey = studentId + " - " + courseId + " - " + batchId;

            // Fetch referenced entities
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

            BatchClassYearSemester batch = batchRepository.findById(batchId)
                    .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found: " + batchId));

            CourseSource source = courseSourceRepository.findById(sourceId)
                    .orElseThrow(() -> new ResourceNotFoundException("CourseSource not found: " + sourceId));

            // Check for duplicate entry
            boolean exists = scoreRepository.existsByStudentAndCourseAndBatchClassYearSemesterAndCourseSource(
                    student, course, batch, source);

            if (exists) {
                String errorMsg = "Duplicate entry already exists";
                logFailure(rawStudentId, rawCourseId, rawBatchId, errorMsg);
                failedRecords.add(recordKey + " (" + errorMsg + ")");
                continue;
            }

            // Parse score – can be null/empty
            Double parsedScore = parseDouble(dto.getScore());

            // Determine isReleased
            boolean isReleased;
            String isReleasedRaw = dto.getIs_released() != null ? dto.getIs_released().trim() : "";
            if ("1".equals(isReleasedRaw)) {
                isReleased = true;
            } else if ("0".equals(isReleasedRaw)) {
                isReleased = false;
            } else {
                // Default rule: released only if a score value exists
                isReleased = parsedScore != null;
            }

            // Create and save entity
            StudentCourseScore scoreEntity = new StudentCourseScore();
            scoreEntity.setStudent(student);
            scoreEntity.setCourse(course);
            scoreEntity.setBatchClassYearSemester(batch);
            scoreEntity.setCourseSource(source);
            scoreEntity.setScore(parsedScore);
            scoreEntity.setReleased(isReleased);

            scoreRepository.save(scoreEntity);
            successfulCount++;

            System.out.println("[" + (i + 1) + "/" + dtoList.size() + "] SUCCESS: " + recordKey +
                    " | Score: " + parsedScore +
                    " | Released: " + isReleased);

        } catch (NumberFormatException e) {
            // Triggered by parseLong / parseDouble when value is empty or not numeric
            String field = e.getMessage(); // contains the field name we passed
            String errorMsg = "Invalid number format in " + field;
            logFailure(rawStudentId, rawCourseId, rawBatchId, errorMsg);
            failedRecords.add(recordKey + " (" + errorMsg + ")");
        } catch (ResourceNotFoundException | BadRequestException e) {
            logFailure(rawStudentId, rawCourseId, rawBatchId, e.getMessage());
            failedRecords.add(recordKey + " (" + e.getMessage() + ")");
        } catch (Exception e) {
            String errorMsg = "Unexpected error: " + e.getClass().getSimpleName() + " - " + e.getMessage();
            logFailure(rawStudentId, rawCourseId, rawBatchId, errorMsg);
            failedRecords.add(recordKey + " (" + errorMsg + ")");
        }
    }

    System.out.println("=== BULK IMPORT FINISHED ===");
    System.out.println("Successful imports: " + successfulCount);
    System.out.println("Failed records     : " + failedRecords.size());
    System.out.println("=====================================");

    return new BulkImportResult(successfulCount, failedRecords);
}

// Helper methods (place them inside the same service class)

private Long parseLong(String value, String fieldName) {
    if (value == null || value.isBlank()) {
        throw new NumberFormatException(fieldName);
    }
    try {
        return Long.parseLong(value);
    } catch (NumberFormatException e) {
        throw new NumberFormatException(fieldName);
    }
}

private Double parseDouble(String value) {
    if (value == null || value.isBlank()) {
        return null;
    }
    try {
        return Double.parseDouble(value);
    } catch (NumberFormatException e) {
        throw new NumberFormatException("score");
    }
}

private void logFailure(String studentId, String courseId, String batchId, String error) {
    System.out.println("============ " + studentId + " ============");
    System.out.println("* " + studentId);
    System.out.println("* " + courseId);
    System.out.println("* " + batchId);
    System.out.println("* " + error);
    System.out.println(); // blank line for readability
}
}