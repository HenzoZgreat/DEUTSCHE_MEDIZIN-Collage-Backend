package Henok.example.DeutscheCollageBack_endAPI.DTO;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.MaritalStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TeacherResponseDTO {

    private Long id;
    private String username;

    private String firstNameAmharic;
    private String lastNameAmharic;
    private String firstNameEnglish;
    private String lastNameEnglish;

    private Gender gender;
    private LocalDate dateOfBirthGC;
    private String dateOfBirthEC;

    private String phoneNumber;
    private String email;

    private String departmentName;
    private LocalDate hireDateGC;
    private String hireDateEC;
    private String title;
    private Integer yearsOfExperience;

    private String impairment;
    private MaritalStatus maritalStatus;
    private String currentAddress;

    private String photographBase64;
}