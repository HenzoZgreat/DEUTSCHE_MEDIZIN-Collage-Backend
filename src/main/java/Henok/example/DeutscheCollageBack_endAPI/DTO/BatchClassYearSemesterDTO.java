package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchClassYearSemesterDTO {

    private Long bcysId;
    private Long batchId;
    private Long classYearId;
    private String semesterId;
    private String entryYearId;
    private LocalDate classStartGC;
    private String classStartEC;
    private LocalDate classEndGC;
    private String classEndEC;
    private Long gradingSystemId;

    // Explanation: DTO for BatchClassYearSemester to expose fields in API requests/responses.
    // Why: Maps entity fields for RESTful operations; avoids exposing entity internals.
}
