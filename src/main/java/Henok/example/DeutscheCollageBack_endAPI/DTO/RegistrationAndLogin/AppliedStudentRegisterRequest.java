package Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppliedStudentRegisterRequest {

    // Personal Information
    private String firstNameAMH;
    private String firstNameENG;
    private String fatherNameAMH;
    private String fatherNameENG;
    private String grandfatherNameAMH;
    private String grandfatherNameENG;
    private String motherNameAMH;
    private String motherNameENG;
    private String motherFatherNameAMH;
    private String motherFatherNameENG;

    // Demographic Information
    private Gender gender;
    private Integer age;
    private String phoneNumber;
    private String dateOfBirthEC;
    private LocalDate dateOfBirthGC;

    // Place of Birth (Foreign Key IDs)
    private String placeOfBirthWoredaCode;
    private String placeOfBirthZoneCode;
    private String placeOfBirthRegionCode;

    // Current Address (Foreign Key IDs)
    private String currentAddressWoredaCode;
    private String currentAddressZoneCode;
    private String currentAddressRegionCode;

    // Additional Personal Information
    private String email;
    private MaritalStatus maritalStatus;
    private String impairmentCode; // Optional
    private Long schoolBackgroundId;

    // Emergency Contact Information
    private String contactPersonFirstNameAMH;
    private String contactPersonFirstNameENG;
    private String contactPersonLastNameAMH;
    private String contactPersonLastNameENG;
    private String contactPersonPhoneNumber;
    private String contactPersonRelation; // Optional

    // Academic Preferences
    private Long departmentEnrolledId;
    private String programModalityCode;
    private Long classYearId;
    private String semesterCode;

    // Document (to be uploaded as MultipartFile in controller)
}