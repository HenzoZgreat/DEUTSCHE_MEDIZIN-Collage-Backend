package Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


// DeanViceDeanRegisterRequest DTO
// Used for registering both Dean and Vice-Dean.
// Contains all personal and administrative details required for DeanViceDeanDetails entity.
// Validation annotations ensure data integrity before processing.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeanViceDeanRegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 128, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name (Amharic) is required")
    @Size(max = 100)
    private String firstNameAMH;

    @NotBlank(message = "First name (English) is required")
    @Size(max = 100)
    private String firstNameENG;

    @NotBlank(message = "Father name (Amharic) is required")
    @Size(max = 100)
    private String fatherNameAMH;

    @NotBlank(message = "Father name (English) is required")
    @Size(max = 100)
    private String fatherNameENG;

    @NotBlank(message = "Grandfather name (Amharic) is required")
    @Size(max = 100)
    private String grandfatherNameAMH;

    @NotBlank(message = "Grandfather name (English) is required")
    @Size(max = 100)
    private String grandfatherNameENG;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Size(max = 100)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(max = 15)
    private String phoneNumber;

    @NotBlank(message = "Residence Woreda code is required")
    @Size(max = 20)
    private String residenceWoredaCode;

    @NotBlank(message = "Residence Zone code is required")
    @Size(max = 20)
    private String residenceZoneCode;

    @NotBlank(message = "Residence Region code is required")
    @Size(max = 20)
    private String residenceRegionCode;

    @NotNull(message = "Hired date (GC) is required")
    private LocalDate hiredDateGC;

    @Size(max = 50)
    private String title; // e.g., Dr., Prof. - optional

    @Size(max = 500)
    private String remarks; // optional administrative notes
}
