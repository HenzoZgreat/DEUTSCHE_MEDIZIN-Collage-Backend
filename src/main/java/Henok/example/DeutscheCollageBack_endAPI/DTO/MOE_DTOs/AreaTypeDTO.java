package Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AreaTypeDTO {
    @JsonProperty("Area Type Code")
    private String areaTypeCode;

    @JsonProperty("Area Type")
    private String areaType;
}