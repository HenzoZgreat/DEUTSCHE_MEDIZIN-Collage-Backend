package Henok.example.DeutscheCollageBack_endAPI.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnrollmentTypeDTO {
    @JsonProperty("Enrollment Type Code")
    private String enrollmentTypeCode;

    @JsonProperty("Enrollment Type Name")
    private String enrollmentTypeName;
}