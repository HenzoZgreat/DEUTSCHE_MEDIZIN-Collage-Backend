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
     * Bulk imports student course scores.
     * - Logs every processed record using System.out.println
     * - Sets isReleased = true if score is present (non-null), false if score is null
     * - Skips invalid records but continues with the rest
     * - Logs detailed failure blocks and collects short identifiers for response
     */
    public BulkImportResult bulkImportScores(List<StudentCourseScoreImportDTO> dtoList) {
        List<String> failedRecords = new ArrayList<>();
        int successfulCount = 0;

        System.out.println("=== STARTING BULK IMPORT OF " + dtoList.size() + " RECORDS ===");

        for (int i = 0; i < dtoList.size(); i++) {
            StudentCourseScoreImportDTO dto = dtoList.get(i);
            String recordKey = dto.getStudentId() + " - " + dto.getCourseId() + " - " + dto.getBatchClassYearSemesterId();

            try {
                // Fetch required entities
                User student = userRepository.findById(dto.getStudentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + dto.getStudentId()));

                Course course = courseRepository.findById(dto.getCourseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + dto.getCourseId()));

                BatchClassYearSemester batch = batchRepository.findById(dto.getBatchClassYearSemesterId())
                        .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found: " + dto.getBatchClassYearSemesterId()));

                CourseSource source = courseSourceRepository.findById(dto.getSourceId())
                        .orElseThrow(() -> new ResourceNotFoundException("CourseSource not found: " + dto.getSourceId()));

                // Check for duplicate
                boolean exists = scoreRepository.existsByStudentAndCourseAndBatchClassYearSemesterAndCourseSource(
                        student, course, batch, source);

                if (exists) {
                    String errorMsg = "Duplicate entry already exists";
                    logFailure(dto, errorMsg);
                    failedRecords.add(recordKey + " (" + errorMsg + ")");
                    continue;
                }

                // Create and populate entity
                StudentCourseScore scoreEntity = new StudentCourseScore();
                scoreEntity.setStudent(student);
                scoreEntity.setCourse(course);
                scoreEntity.setBatchClassYearSemester(batch);
                scoreEntity.setCourseSource(source);
                scoreEntity.setScore(dto.getScore());

                // Rule: isReleased = true if score present, false if score is null
                boolean released = dto.getScore() != null;
                scoreEntity.setReleased(released);

                // Optional override from DTO (if client explicitly sends isReleased)
                if (dto.getIsReleased() != null) {
                    scoreEntity.setReleased(dto.getIsReleased());
                }

                scoreRepository.save(scoreEntity);
                successfulCount++;

                System.out.println("[" + (i + 1) + "/" + dtoList.size() + "] SUCCESS: " + recordKey +
                        " | Score: " + dto.getScore() +
                        " | Released: " + scoreEntity.isReleased());

            } catch (ResourceNotFoundException | BadRequestException e) {
                logFailure(dto, e.getMessage());
                failedRecords.add(recordKey + " (" + e.getMessage() + ")");
            } catch (Exception e) {
                String msg = "Unexpected error: " + e.getClass().getSimpleName() + " - " + e.getMessage();
                logFailure(dto, msg);
                failedRecords.add(recordKey + " (" + msg + ")");
            }
        }

        System.out.println("=== BULK IMPORT FINISHED ===");
        System.out.println("Successful: " + successfulCount);
        System.out.println("Failed: " + failedRecords.size());
        System.out.println("=====================================");

        return new BulkImportResult(successfulCount, failedRecords);
    }

    // Helper to print detailed failure block exactly as requested
    private void logFailure(StudentCourseScoreImportDTO dto, String error) {
        System.out.println("============ " + dto.getStudentId() + " ============");
        System.out.println("* " + dto.getStudentId());
        System.out.println("* " + dto.getCourseId());
        System.out.println("* " + dto.getBatchClassYearSemesterId());
        System.out.println("* " + error);
        System.out.println(); // empty line for separation
    }
}