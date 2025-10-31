package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourseResponse {

    private String courseTitle;
    private String courseCode;
    private Integer creditHours;        // theoryHrs + labHrs
    private Long totalStudents;         // count of students in this BCYS
    private String bcysDisplay;         // e.g., "2024-2025-S1"
}