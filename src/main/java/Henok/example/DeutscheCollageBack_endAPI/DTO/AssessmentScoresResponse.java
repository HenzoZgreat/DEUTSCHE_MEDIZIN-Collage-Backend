package Henok.example.DeutscheCollageBack_endAPI.DTO;

import Henok.example.DeutscheCollageBack_endAPI.Enums.AssessmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// AssessmentScoresResponse (Main DTO matching the exact JSON structure)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentScoresResponse {

    private String message = "Student scores retrieved successfully";

    private Long teacherCourseAssignmentId;
    private String teacherName;

    private String courseCode;

    private String courseTitle;

    private String batchClassYearSemester;

    private List<AssessmentInfo> assessments = new ArrayList<>();

    private List<StudentScoreView> students = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleScore {
        private Long assessmentId;
        private Double score;               // null if not graded yet
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentInfo {
        private Long assessmentId;
        private String title;
        private Double maxScore;
        private LocalDateTime dueDate;
        private AssessmentStatus status;
        private AssessmentStatus headApproval;
        private AssessmentStatus registrarApproval;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentScoreView {
        private Long studentId;
        private String studentIdNumber;     // e.g., STU/2022/101 – adjust field name if different in your entity
        private String fullNameENG;
        private String fullNameAMH;
        private List<SingleScore> scores = new ArrayList<>();
    }
}





