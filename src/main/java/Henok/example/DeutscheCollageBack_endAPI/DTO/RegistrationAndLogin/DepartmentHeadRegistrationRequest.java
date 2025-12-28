package Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

// DepartmentHeadRegistrationRequest DTO
// Why: Separate DTO to capture registration data without exposing entities.
// Includes password (plain text) and confirmation for validation.
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentHeadRegistrationRequest {

    @Size(max = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Size(max = 128)
    private String passwordConfirm;

    // Personal details
    @NotBlank(message = "First name (English) is required")
    @Size(max = 100)
    private String firstNameENG;

    @NotBlank(message = "First name (Amharic) is required")
    @Size(max = 100)
    private String firstNameAMH;

    @NotBlank(message = "Father name (English) is required")
    @Size(max = 100)
    private String fatherNameENG;

    @NotBlank(message = "Father name (Amharic) is required")
    @Size(max = 100)
    private String fatherNameAMH;

    @NotBlank(message = "Grandfather name (English) is required")
    @Size(max = 100)
    private String grandfatherNameENG;

    @NotBlank(message = "Grandfather name (Amharic) is required")
    @Size(max = 100)
    private String grandfatherNameAMH;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Size(max = 15)
    private String phoneNumber;

    @Size(max = 100)
    private String email;

    @NotNull(message = "Hired date (GC) is required")
    private LocalDate hiredDateGC;

    @Size(max = 20)
    private String hiredDateEC;

    @NotNull(message = "Department ID is required")
    @Digits(integer = 19, fraction = 0)
    private Long departmentId;

    // Residence address
    @NotBlank(message = "Residence region code is required")
    @Size(max = 20)
    private String residenceRegionCode;

    @NotBlank(message = "Residence zone code is required")
    @Size(max = 20)
    private String residenceZoneCode;

    @NotBlank(message = "Residence woreda code is required")
    @Size(max = 20)
    private String residenceWoredaCode;

    private MultipartFile photo;       // max 2MB - validated in service
    private MultipartFile documents;   // max 5MB - validated in service

    @Size(max = 500)
    private String remark;             // Optional remark
}