package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// StudentScoreRequest (DTO)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentScoreRequest {

    private Long assessmentId;

    private Long studentId;

    private Double score;
}