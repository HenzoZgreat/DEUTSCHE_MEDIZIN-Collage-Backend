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

    private String versionName;
    private List<MarkIntervalDTO> intervals;

    // Explanation: DTO for GradingSystem to expose only versionName and intervals in API requests/responses.
    // Why: Excludes id (auto-generated) and effectiveDate (auto-filled in service); avoids exposing entity internals.
}
