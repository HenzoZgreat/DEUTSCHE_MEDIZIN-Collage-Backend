package Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


// Shared DTO for both record and update bulk operations
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkStudentScoreRequest {
    private List<SingleScore> scores = new ArrayList<>();

    // Nested class – used by both create and update
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleScore {
        private Long assessmentId;
        private Long studentId;
        private Double score;   // required, >= 0 and <= maxScore
    }
}