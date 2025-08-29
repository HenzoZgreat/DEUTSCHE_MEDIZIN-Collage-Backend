package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradingSystemDTO {

    private Long id;
    private String versionName;
    private LocalDate effectiveDate;
    private List<MarkIntervalDTO> intervals;

    // Explanation: DTO for GradingSystem to expose only necessary fields in API responses/requests.
    // Why: Avoids exposing entity internals; supports nested intervals for full representation.
}
