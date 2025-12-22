package Henok.example.DeutscheCollageBack_endAPI.DTO.DeanAndVice_Dean;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

// DeanViceDeanListDTO
// DTO for listing active Deans/Vice-Deans.
// Includes hasDocument instead of documents, and photo as byte[].
@Data
public class DeanViceDeanListDTO {
    private Long id;
    private String username; // From User
    private String firstNameAMH;
    private String firstNameENG;
    private String fatherNameAMH;
    private String fatherNameENG;
    private String grandfatherNameAMH;
    private String grandfatherNameENG;
    private Gender gender;
    private String email;
    private String phoneNumber;
    private String residenceRegion; // Name from Region entity
    private String residenceZone;   // Name from Zone entity
    private String residenceWoreda; // Name from Woreda entity
    private LocalDate hiredDateGC;
    private String title;
    private String remarks; // Included for admin listing
    private boolean hasDocument;
    private byte[] photo;
    private Role role; // From User
}

