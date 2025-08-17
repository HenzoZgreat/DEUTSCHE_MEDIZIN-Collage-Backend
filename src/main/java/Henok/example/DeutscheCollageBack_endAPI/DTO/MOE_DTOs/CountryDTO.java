package Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CountryDTO {
    @JsonProperty("Country Code")
    private String countryCode;

    @JsonProperty("Country")
    private String country;
}