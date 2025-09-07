package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.Data;

import java.time.LocalDate;

// DTO to hold all StudentDetails fields, including photo and document
// Why: Prevents exposing entity internals, provides all data for retrieval
@Data
public class StudentDetailsDTO {
    private Long id;
    private Long userId;
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
    private String gender;
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
    private String maritalStatus;
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
    private Long studentRecentStatusId;
    private Long departmentEnrolledId;
    private String programModalityCode;
    private byte[] document;
    private String documentStatus;
    private String remark;
    private Boolean isTransfer;
    private String exitExamUserID;
    private Double exitExamScore;
    private Boolean isStudentPassExitExam;
    private Double grade12Result;
}
