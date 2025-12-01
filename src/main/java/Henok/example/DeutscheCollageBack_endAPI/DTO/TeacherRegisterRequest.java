package Henok.example.DeutscheCollageBack_endAPI.DTO;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRegisterRequest {

    private String username;
    private String password;

    private String firstNameAmharic;
    private String lastNameAmharic;
    private String firstNameEnglish;
    private String lastNameEnglish;

    private Gender gender;
    private LocalDate dateOfBirthGC;
    private String dateOfBirthEC;

    private String phoneNumber;
    private String email;

    private Long departmentId;
    private LocalDate hireDateGC;
    private String hireDateEC;
    private String title;
    private Integer yearsOfExperience;

    private String impairmentCode;
    private MaritalStatus maritalStatus;

    private String currentAddressWoredaCode;
    private String currentAddressZoneCode;
    private String currentAddressRegionCode;

    // New: List of course + BCYS assignments
    private List<AssignTeacherCoursesRequest> courseAssignments = new ArrayList<>();
}