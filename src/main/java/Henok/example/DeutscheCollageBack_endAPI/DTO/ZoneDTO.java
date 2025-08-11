package Henok.example.DeutscheCollageBack_endAPI.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZoneDTO {
    @JsonProperty("Zone Code")
    private String zoneCode;

    @JsonProperty("Zone")
    private String zone;

    @JsonProperty("Region Code")
    private String regionCode;
}