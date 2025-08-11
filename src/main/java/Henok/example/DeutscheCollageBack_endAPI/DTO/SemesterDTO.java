package Henok.example.DeutscheCollageBack_endAPI.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SemesterDTO {
    @JsonProperty("Academic Period Code")
    private String academicPeriodCode;

    @JsonProperty("Academic Period")
    private String academicPeriod;
}