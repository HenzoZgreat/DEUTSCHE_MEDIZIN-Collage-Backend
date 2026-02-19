package Henok.example.DeutscheCollageBack_endAPI.DTO.Students;

import lombok.Builder;
import lombok.Data;

// RemainingCourseDTO.java
@Data
@Builder
public class RemainingCourseDTO {
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private int creditHours;
    private String expectedIn;         // just "Year 2 - Semester 1"
}
