package Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfileResponse {

    private Long userId;
    private String username;
    private String fullNameEnglish;
    private String fullNameAmharic;

    private Gender gender;                    // enum
    private LocalDate dateOfBirthGC;
    private String dateOfBirthEC;
    private String phoneNumber;
    private String email;
    private MaritalStatus maritalStatus;      // enum

    private String photoBase64;

    // Current address flattened
    private String currentAddressRegion;
    private String currentAddressZone;
    private String currentAddressWoreda;

    private String impairment;                 // name only
    private String department;                 // department name
    private String title;
    private Integer yearsOfExperience;
    private LocalDate hireDateGC;
    private String hireDateEC;
}