package Henok.example.DeutscheCollageBack_endAPI.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

// AssessmentCreateRequest (DTO)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentCreateRequest {

    private Long teacherCourseAssignmentId;

    private String assTitle;

    private Double maxScore;

    private LocalDateTime dueDate;

    private String description;
}