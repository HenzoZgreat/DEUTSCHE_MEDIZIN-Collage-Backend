package Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


// Request for bulk create
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkAssessmentRequest {
    private Long teacherCourseAssignmentId;
    private List<SingleAssessmentCreate> assessments = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleAssessmentCreate {
        private String assTitle;
        private Double maxScore;
        private LocalDateTime dueDate;
        private String description;
    }
}