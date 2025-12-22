package Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


// Request for bulk update
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkAssessmentUpdateRequest {
    private List<SingleAssessmentUpdate> assessments = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleAssessmentUpdate {
        private Long assessmentId;
        private String assTitle;        // optional
        private Double maxScore;        // optional
        private LocalDateTime dueDate;  // optional
        private String description;     // optional
    }
}