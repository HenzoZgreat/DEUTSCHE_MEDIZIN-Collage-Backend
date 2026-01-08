package Henok.example.DeutscheCollageBack_endAPI.migration.DTO;

import lombok.Data;

@Data
public class StudentCourseScoreImportDTO {
    // ID of the student (references User entity)
// Why: Used to link the score to a specific student via foreign key.
// Required: Must not be null for valid import.
    private Long studentId;
    // ID of the course (references Course entity)
// Why: Identifies which course this score belongs to.
// Required: Must not be null for valid import.
    private Long courseId;
    // ID of the batch/class/year/semester (references BatchClassYearSemester entity)
// Why: Specifies the academic period/context for this score.
// Required: Must not be null for valid import.
    private Long batchClassYearSemesterId;
    // ID of the course source (references CourseSource entity)
// Why: Indicates the origin or type of the course (e.g., regular, elective).
// Required: Must not be null for valid import.
    private Long sourceId;
    // The score achieved by the student in the course
// Why: Core value being imported; represents student's performance.
// Optional: Can be null if not yet available or to be set later.
    private Double score;
    // Flag indicating if the score has been officially released
// Why: Controls visibility or finality of the score; defaults to false in entity.
// Optional: Can be null; if null, will default to false during mapping.
    private Boolean isReleased;
}
