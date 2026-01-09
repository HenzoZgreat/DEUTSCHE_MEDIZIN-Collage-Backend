package Henok.example.DeutscheCollageBack_endAPI.migration.DTO;

import lombok.Data;

// Updated DTO - StudentCourseScoreImportDTO
// Why the changes?
// The incoming JSON uses String values for all fields (including IDs and score).
// We keep fields as String to avoid Jackson deserialization errors on malformed data.
// During processing in the service, we safely parse them to Long/Double/Boolean.

@Data
public class StudentCourseScoreImportDTO {

    // Student user ID - received as String (e.g. "29")
    // Why String? Incoming data is quoted as string; parsing happens in service for better error control.
    private String student_user_id;

    // Course ID - received as String (e.g. "136")
    private String course_id;

    // Batch Class Year Semester ID - received as String (e.g. "14")
    private String batch_class_year_semester_id;

    // Course Source ID - received as String (e.g. "1")
    private String source_id;

    // Score - received as String (e.g. "94.00" or "0.00")
    // Kept as String to handle leading/trailing spaces or invalid formats gracefully.
    private String score;

    // Is Released flag - received as String "0" or "1"
    // Why String? The source system sends 0/1 as text, not boolean.
    private String is_released;
}