package Henok.example.DeutscheCollageBack_endAPI.DTO.DeanAndVice_Dean;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// DeanViceDeanResponse DTO
// Response DTO for Dean/Vice-Dean details.
// Excludes sensitive fields like password.
// Replaces documents with hasDocument boolean.
// Encodes photo as Base64 string for safe JSON transmission.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeanViceDeanResponse {

    private Long id;
    private Long userId;
    private String username;
    private Role role;
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
    private String photo; // Base64 encoded string
    private boolean hasDocument; // True if documents exist
    private boolean active;
}
