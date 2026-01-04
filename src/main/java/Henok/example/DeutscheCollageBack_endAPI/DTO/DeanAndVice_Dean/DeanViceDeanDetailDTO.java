package Henok.example.DeutscheCollageBack_endAPI.DTO.DeanAndVice_Dean;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import lombok.Data;

import java.time.LocalDate;

// DeanViceDeanDetailDTO
// DTO used for retrieving detailed information about a Dean or Vice-Dean by ID.
// Excludes photo and document bytes; instead provides boolean flags.
// Includes residence location names and codes for frontend display/search.
@Data
public class DeanViceDeanDetailDTO {
    private Long id;
    private Long userId;
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

    // Residence details with both name and code
    private String residenceRegion;
    private String residenceRegionCode;
    private String residenceZone;
    private String residenceZoneCode;
    private String residenceWoreda;
    private String residenceWoredaCode;

    private LocalDate hiredDateGC;
    private String title;
    private String remarks; // Included for admin view
    private boolean hasPhoto;
    private boolean hasDocument;
    private Role role;
    private boolean active;
}
