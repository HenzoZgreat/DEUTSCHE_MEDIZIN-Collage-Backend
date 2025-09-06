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
    private String name; // Constructed as [batchName]-[classYear]-[semesterCode] for GET requests

    // Explanation: DTO for BatchClassYearSemester with name field for user-friendly display.
    // Why: Name field (batchName-classYear-semesterCode) added for GET responses only.
}