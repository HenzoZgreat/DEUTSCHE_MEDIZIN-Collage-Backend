package Henok.example.DeutscheCollageBack_endAPI.DTO.GradeReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for grade report request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeReportRequestDTO {
    private List<Long> studentIds; // List of StudentDetails IDs
}

