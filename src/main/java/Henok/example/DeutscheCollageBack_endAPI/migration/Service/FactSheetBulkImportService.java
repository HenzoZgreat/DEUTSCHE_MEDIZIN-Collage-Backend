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
     * Memory-safe bulk import for very large JSON payloads.
     *
     * Changes for production (Railway):
     * - NO logging for successful records (prevents huge console output → OOM).
     * - Only failed records are logged in the detailed block format you requested.
     * - Minimal progress logging every 1000 records.
     * - All parsing and processing is done record-by-record with minimal object retention.
     */
    public BulkImportResult bulkImportScores(List<StudentCourseScoreImportDTO> dtoList) {
        List<String> failedRecords = new ArrayList<>();
        int successfulCount = 0;
        int total = dtoList.size();

        System.out.println("=== STARTING BULK IMPORT OF " + total + " RECORDS ===");

        for (int i = 0; i < total; i++) {
            StudentCourseScoreImportDTO dto = dtoList.get(i);

            // Use raw string values directly – avoid extra object creation
            String rawStudentId = dto.getStudent_user_id() != null ? dto.getStudent_user_id().trim() : "";
            String rawCourseId = dto.getCourse_id() != null ? dto.getCourse_id().trim() : "";
            String rawBatchId = dto.getBatch_class_year_semester_id() != null ? dto.getBatch_class_year_semester_id().trim() : "";

            String recordKey = rawStudentId + " - " + rawCourseId + " - " + rawBatchId;

            try {
                // Parse required IDs
                Long studentId = parseLong(rawStudentId, "student_user_id");
                Long courseId = parseLong(rawCourseId, "course_id");
                Long batchId = parseLong(rawBatchId, "batch_class_year_semester_id");
                Long sourceId = parseLong(dto.getSource_id(), "source_id");

                // Use parsed values for cleaner response key
                recordKey = studentId + " - " + courseId + " - " + batchId;

                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

                BatchClassYearSemester batch = batchRepository.findById(batchId)
                        .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found: " + batchId));

                CourseSource source = courseSourceRepository.findById(sourceId)
                        .orElseThrow(() -> new ResourceNotFoundException("CourseSource not found: " + sourceId));

                // Duplicate check
                boolean exists = scoreRepository.existsByStudentAndCourseAndBatchClassYearSemesterAndCourseSource(
                        student, course, batch, source);

                if (exists) {
                    String errorMsg = "Duplicate entry already exists";
                    logFailure(rawStudentId, rawCourseId, rawBatchId, errorMsg);
                    failedRecords.add(recordKey + " (" + errorMsg + ")");
                    continue;
                }

                // Parse score
                Double parsedScore = parseDouble(dto.getScore());

                // isReleased logic
                boolean isReleased;
                String isReleasedRaw = dto.getIs_released() != null ? dto.getIs_released().trim() : "";
                if ("1".equals(isReleasedRaw)) {
                    isReleased = true;
                } else if ("0".equals(isReleasedRaw)) {
                    isReleased = false;
                } else {
                    isReleased = parsedScore != null; // true only if score exists
                }

                // Save entity
                StudentCourseScore scoreEntity = new StudentCourseScore();
                scoreEntity.setStudent(student);
                scoreEntity.setCourse(course);
                scoreEntity.setBatchClassYearSemester(batch);
                scoreEntity.setCourseSource(source);
                scoreEntity.setScore(parsedScore);
                scoreEntity.setReleased(isReleased);

                scoreRepository.save(scoreEntity);
                successfulCount++;

            } catch (NumberFormatException e) {
                String field = e.getMessage();
                String errorMsg = "Invalid number format in " + field;
                logFailure(rawStudentId, rawCourseId, rawBatchId, errorMsg);
                failedRecords.add(recordKey + " (" + errorMsg + ")");
            } catch (ResourceNotFoundException | BadRequestException e) {
                logFailure(rawStudentId, rawCourseId, rawBatchId, e.getMessage());
                failedRecords.add(recordKey + " (" + e.getMessage() + ")");
            } catch (Exception e) {
                String errorMsg = "Unexpected error: " + e.getClass().getSimpleName();
                logFailure(rawStudentId, rawCourseId, rawBatchId, errorMsg);
                failedRecords.add(recordKey + " (" + errorMsg + ")");
            }

            // Minimal progress logging – safe even for 100k+ records
            if ((i + 1) % 1000 == 0 || (i + 1) == total) {
                System.out.println("Processed " + (i + 1) + "/" + total + " records | Success: " + successfulCount + " | Failed: " + failedRecords.size());
            }
        }

        System.out.println("=== BULK IMPORT COMPLETED ===");
        System.out.println("Total processed : " + total);
        System.out.println("Successful      : " + successfulCount);
        System.out.println("Failed          : " + failedRecords.size());
        System.out.println("=====================================");

        return new BulkImportResult(successfulCount, failedRecords);
    }

    // Keep these helper methods unchanged
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
        System.out.println();
    }
}