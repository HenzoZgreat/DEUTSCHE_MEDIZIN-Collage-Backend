package Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImpairmentDTO {
    @JsonProperty("Disability Code")
    private String disabilityCode;

    @JsonProperty("Disability")
    private String disability;
}
