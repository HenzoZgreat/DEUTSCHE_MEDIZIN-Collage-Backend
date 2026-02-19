package Henok.example.DeutscheCollageBack_endAPI.DTO.Students;

import lombok.Builder;
import lombok.Data;

// TakenCourseDTO.java
@Data
@Builder
public class TakenCourseDTO {
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private int creditHours;
    private String courseSource;       // sourceName
    private boolean isReleased;
    private String takenIn;            // formatted BCYS like "2022-Year 1-Semester 1"
}
