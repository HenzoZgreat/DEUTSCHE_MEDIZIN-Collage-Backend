package Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AcademicYearDTO {
    @JsonProperty("Academic Year Code")
    private String academicYearCode;

    @JsonProperty("Academic Year GC")
    private String academicYearGC;

    @JsonProperty("Academic Year EC")
    private String academicYearEC;
}
