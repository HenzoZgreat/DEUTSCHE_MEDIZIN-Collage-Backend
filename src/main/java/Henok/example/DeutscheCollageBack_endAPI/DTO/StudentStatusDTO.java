package Henok.example.DeutscheCollageBack_endAPI.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StudentStatusDTO {
    @JsonProperty("statusName")
    private String statusName;
}