package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

// AssignedCourseDTO.java (nested DTO for courses in detail view)
@Data
@NoArgsConstructor
public class AssignedCourseDTO {

    private Long teacherCourseAssigmentId; // Assignment ID
    private Long id;                        // Course ID
    private String courseCode;              // Course code
    private String courseTitle;             // Course title
    private Integer totalCrHrs;             // lab + theory (assuming Course has labHrs + theoryHrs or creditHours)
    private Long batchClassYearSemesterId;  // BCYS ID
    private String batchClassYearSemesterName;  // BCYS name (e.g., "Batch1-Year2-Semester1")
}