package Henok.example.DeutscheCollageBack_endAPI.DTO.DeanAndVice_Dean;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

// DeanViceDeanUpdateRequest
// DTO for partial updates.
// All fields are optional (no @NotNull/@NotBlank) to allow selective updates.
// Validations only on formats where applicable (e.g., @Size for password if provided).
@Data
public class DeanViceDeanUpdateRequest {

    private String firstNameAMH;
    private String firstNameENG;
    private String fatherNameAMH;
    private String fatherNameENG;
    private String grandfatherNameAMH;
    private String grandfatherNameENG;
    private Gender gender;
    private String email;

    private String phoneNumber;

    private String residenceWoredaCode;
    private String residenceZoneCode;
    private String residenceRegionCode;

    private LocalDate hiredDateGC;

    private String title;

    private String remarks;
}