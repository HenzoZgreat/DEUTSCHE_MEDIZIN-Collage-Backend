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
public class StudentRegisterRequest {
    // User fields
    private String username;
    private String password;

    // StudentDetails fields
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
    private byte[] studentPhoto;
    private String contactPersonFirstNameAMH;
    private String contactPersonFirstNameENG;
    private String contactPersonLastNameAMH;
    private String contactPersonLastNameENG;
    private String contactPersonPhoneNumber;
    private String contactPersonRelation;
    private String dateEnrolledEC;
    private LocalDate dateEnrolledGC;
    private Long batchClassYearSemesterId;
    private Long departmentEnrolledId;
    private String programModalityCode;
    private Long studentRecentStatusId;
    private byte[] document;
    private boolean isTransfer;
    private String exitExamUserID;
    private Double exitExamScore;
    private boolean isStudentPassExitExam;
}
