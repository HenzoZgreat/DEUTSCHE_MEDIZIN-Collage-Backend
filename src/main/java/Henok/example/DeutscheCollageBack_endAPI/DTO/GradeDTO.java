package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeDTO {

    private String letterGrade;
    private double gpaValue;

    // Explanation: Simple DTO to return computed grade (letter and GPA value).
    // Why: Used in grade calculation responses; keeps output structured.
}