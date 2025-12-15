package Henok.example.DeutscheCollageBack_endAPI.DTO.Student;

import Henok.example.DeutscheCollageBack_endAPI.Enums.DocumentStatus;
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
public class StudentProfileResponse {

    private Long userId;
    private String username;
    private String fullNameEnglish;
    private String fullNameAmharic;

    private Gender gender;                    // enum
    private Integer age;
    private String phoneNumber;
    private String email;
    private LocalDate dateOfBirthGC;
    private String dateOfBirthEC;
    private MaritalStatus maritalStatus;      // enum

    private String photoBase64;

    // Place of Birth (flattened)
    private String placeOfBirthRegion;
    private String placeOfBirthZone;
    private String placeOfBirthWoreda;

    // Current Address (flattened)
    private String currentAddressRegion;
    private String currentAddressZone;
    private String currentAddressWoreda;

    private String impairment;                 // only name
    private String schoolBackground;           // only name

    private String contactPersonFullNameENG;
    private String contactPersonPhoneNumber;
    private String contactPersonRelation;

    private LocalDate dateEnrolledGC;
    private String academicYear;
    private String batchClassYearSemester;
    private String studentRecentStatus;        // name from StudentStatus entity
    private String departmentEnrolled;         // department name
    private String programModality;            // modality name

    private DocumentStatus documentStatus;     // enum
    private Double grade12Result;
}