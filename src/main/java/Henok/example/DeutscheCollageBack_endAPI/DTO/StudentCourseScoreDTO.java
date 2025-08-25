package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseScoreDTO {
    private Long studentId;
    private Long courseId;
    private Long batchClassYearSemesterId;
    private Long sourceId;
    private Double score;
    private Boolean isReleased;
}