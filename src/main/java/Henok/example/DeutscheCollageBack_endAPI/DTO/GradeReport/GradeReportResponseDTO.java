package Henok.example.DeutscheCollageBack_endAPI.DTO.GradeReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for grade report response containing multiple students.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeReportResponseDTO {
    private List<GradeReportDTO> gradeReports;
}

