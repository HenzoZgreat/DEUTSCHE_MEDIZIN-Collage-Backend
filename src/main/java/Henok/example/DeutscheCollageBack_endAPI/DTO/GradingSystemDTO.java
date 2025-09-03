package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradingSystemDTO {

    private String versionName;
    private Long departmentId; // Optional: ID of the department for department-specific systems; null for global
    private String remark; // Optional remark or note about the grading system
    private List<MarkIntervalDTO> intervals;

    // Explanation: DTO for GradingSystem to expose versionName, optional departmentId, remark, and intervals.
    // Why: Allows creating department-specific or global systems; excludes id as it is auto-generated.
}