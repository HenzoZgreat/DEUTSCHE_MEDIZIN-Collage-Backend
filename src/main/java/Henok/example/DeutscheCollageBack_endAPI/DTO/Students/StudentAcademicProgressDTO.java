package Henok.example.DeutscheCollageBack_endAPI.DTO.Students;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// StudentAcademicProgressDTO.java
@Data
@Builder
public class StudentAcademicProgressDTO {

    private Long studentId;
    private String username;
    private String fullName;
    private String department;
    private String currentStatus;
    private String currentBatchClassYearSemester;

    private List<TakenCourseDTO> takenCourses;
    private int totalTakenCourses;
    private int totalTakenCreditHours;

    private List<RemainingCourseDTO> remainingCourses;
    private int totalRemainingCourses;
    private int totalRemainingCreditHours;
}

