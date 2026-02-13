package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressionSequenceDTO {

    private Long id;

    private Long departmentId;
    private String departmentName;
    private String departmentCode;

    private Long classYearId;
    private String classYearName;

    private String semesterId;              // academicPeriodCode
    private String semesterName;

    private Integer sequenceNumber;

    private String description;             // optional
}