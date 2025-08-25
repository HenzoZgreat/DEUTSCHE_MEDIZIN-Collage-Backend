package Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterDTO {

    private String academicPeriodCode;
    private String academicPeriod;
}