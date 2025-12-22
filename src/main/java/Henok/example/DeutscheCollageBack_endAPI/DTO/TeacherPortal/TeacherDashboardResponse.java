package Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// TeacherDashboardResponse (DTO with nested class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardResponse {

    private String message = "Dashboard data retrieved successfully";

    private String teacherNameENG;

    private String teacherNameAMH;

    private String department;

    private int totalAssignedCourses = 0;

    private int totalStudents = 0;

    private long totalAssessmentsCreated = 0;

    private long pendingAssessments = 0;

    private List<CourseSummary> recentCourses = new ArrayList<>();

    // Nested DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseSummary {
        private Long assignmentId;
        private String courseCode;
        private String courseTitle;
        private String batchClassYearSemester;
        private long studentCount;
    }
}