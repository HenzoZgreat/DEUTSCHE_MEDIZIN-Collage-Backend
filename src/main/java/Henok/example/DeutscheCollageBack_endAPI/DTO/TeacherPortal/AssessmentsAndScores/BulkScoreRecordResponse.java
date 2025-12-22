package Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


// Shared response DTO for bulk recording (create new scores)
// Why: Keeps consistent structure with update response (count + failures)
//    but uses "recordedCount" and "failedRecords" to distinguish from update
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkScoreRecordResponse {
    private int recordedCount = 0;
    private List<FailedRecord> failedRecords = new ArrayList<>();

    // Nested class – reused from update if you want, or keep separate for clarity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedRecord {
        private Long assessmentId;
        private Long studentId;
        private String reason;
    }
}