package Henok.example.DeutscheCollageBack_endAPI.DTO;

import Henok.example.DeutscheCollageBack_endAPI.Enums.ApplicationStatus;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppliedStudentResponseDTO {

    private Long id;
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
    private Gender gender;
    private Integer age;
    private String phoneNumber;
    private String dateOfBirthEC;
    private LocalDate dateOfBirthGC;
    private String placeOfBirthWoredaCode;
    private String placeOfBirthZoneCode;
    private String placeOfBirthRegionCode;
    private String currentAddressWoredaCode;
    private String currentAddressZoneCode;
    private String currentAddressRegionCode;
    private String email;
    private MaritalStatus maritalStatus;
    private String impairmentCode;
    private Long schoolBackgroundId;
    private String contactPersonFirstNameAMH;
    private String contactPersonFirstNameENG;
    private String contactPersonLastNameAMH;
    private String contactPersonLastNameENG;
    private String contactPersonPhoneNumber;
    private String contactPersonRelation;
    private Long departmentEnrolledId;
    private String programModalityCode;
    private Long classYearId;
    private String semesterCode;
    private ApplicationStatus applicationStatus;
    private boolean hasPhoto; // Indicates if a student photo was uploaded
    private boolean hasDocument; // Indicates if a document was uploaded
}
