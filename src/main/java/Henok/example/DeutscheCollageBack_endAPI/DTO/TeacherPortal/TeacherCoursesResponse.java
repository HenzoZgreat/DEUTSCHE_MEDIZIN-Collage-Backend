package Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// TeacherCoursesResponse (DTO with nested class as requested)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCoursesResponse {

    private String message = "Assigned courses retrieved successfully";

    private int totalCourses = 0;

    private List<CourseAssignment> courses = new ArrayList<>();

    // Nested DTO – no separate file needed
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseAssignment {
        private Long assignmentId;           // TeacherCourseAssignment.id – used for creating assessments
        private String courseCode;
        private String courseTitle;
        private int theoryHours;
        private int labHours;
        private String department;
        private String batchClassYearSemester; // e.g., "2024/2025 - Year 3 - Semester 1"
        private LocalDateTime assignedAt;
    }
}