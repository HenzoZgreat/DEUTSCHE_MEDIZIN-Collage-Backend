package Henok.example.DeutscheCollageBack_endAPI.DTO.Heads;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

// DepartmentHeadUpdateRequest DTO (for partial update)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentHeadUpdateRequest {

    private String firstNameENG;
    private String firstNameAMH;
    private String fatherNameENG;
    private String fatherNameAMH;
    private String grandfatherNameENG;
    private String grandfatherNameAMH;
    private Gender gender;
    private String phoneNumber;
    private String email;
    private LocalDate hiredDateGC;
    private String hiredDateEC;
    private Long departmentId;                  // to change department
    private String residenceRegionCode;
    private String residenceZoneCode;
    private String residenceWoredaCode;
    private String remark;
    private Boolean isActive;                   // can deactivate/reactivate

    // Files (set from controller)
    private MultipartFile photo;                // new photo replaces old one if provided
    private MultipartFile documents;            // new documents replace old one if provided

}

