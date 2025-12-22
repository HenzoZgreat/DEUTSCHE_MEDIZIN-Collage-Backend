package Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String passwordConfirm;

    // Personal details
    @NotBlank(message = "First name (English) is required")
    private String firstNameENG;

    @NotBlank(message = "First name (Amharic) is required")
    private String firstNameAMH;

    @NotBlank(message = "Father name (English) is required")
    private String fatherNameENG;

    @NotBlank(message = "Father name (Amharic) is required")
    private String fatherNameAMH;

    @NotBlank(message = "Grandfather name (English) is required")
    private String grandfatherNameENG;

    @NotBlank(message = "Grandfather name (Amharic) is required")
    private String grandfatherNameAMH;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private String phoneNumber;

    private String email;

    @NotNull(message = "Hired date (GC) is required")
    private LocalDate hiredDateGC;

    private String hiredDateEC;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    // Residence address
    @NotBlank(message = "Residence region code is required")
    private String residenceRegionCode;

    @NotBlank(message = "Residence zone code is required")
    private String residenceZoneCode;

    @NotBlank(message = "Residence woreda code is required")
    private String residenceWoredaCode;

    private MultipartFile photo;       // max 2MB - validated in service
    private MultipartFile documents;   // max 5MB - validated in service

    private String remark;             // Optional remark
}