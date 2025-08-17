package Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegionDTO {
    @JsonProperty("Region Code")
    private String regionCode;

    @JsonProperty("Region")
    private String region;

    @JsonProperty("Region Type")
    private String regionType;

    @JsonProperty("Region 2nd language")
    private String region2ndLanguage;

    @JsonProperty("Priority Order")
    private String priorityOrder;
}