package Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WoredaDTO {
    @JsonProperty("Woreda Code")
    private String woredaCode;

    @JsonProperty("Woreda")
    private String woreda;

    @JsonProperty("Zone Code")
    private String zoneCode;
}
