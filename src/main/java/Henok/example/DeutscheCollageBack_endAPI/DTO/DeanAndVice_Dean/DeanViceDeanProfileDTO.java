package Henok.example.DeutscheCollageBack_endAPI.DTO.DeanAndVice_Dean;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import lombok.Data;

import java.time.LocalDate;

// DeanViceDeanProfileDTO
// DTO for profile view.
// Includes all details except remarks; includes documents if present.
@Data
public class DeanViceDeanProfileDTO {
    private Long id;
    private String username;
    private String firstNameAMH;
    private String firstNameENG;
    private String fatherNameAMH;
    private String fatherNameENG;
    private String grandfatherNameAMH;
    private String grandfatherNameENG;
    private Gender gender;
    private String email;
    private String phoneNumber;
    private String residenceRegion;
    private String residenceRegionCode;
    private String residenceZone;
    private String residenceZoneCode;
    private String residenceWoreda;
    private String residenceWoredaCode;
    private LocalDate hiredDateGC;
    private String title;
    private byte[] photo;
    private Role role;
}
