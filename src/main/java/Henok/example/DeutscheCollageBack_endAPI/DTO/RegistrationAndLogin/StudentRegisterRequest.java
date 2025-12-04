package Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin;

import Henok.example.DeutscheCollageBack_endAPI.Enums.DocumentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class StudentRegisterRequest {
    // User credentials for registration
    private String username; // Required: Username for login
    private String password; // Required: Password for login

    // Personal Information (Names in Amharic and English)
    private String firstNameAMH; // Required: First name in Amharic
    private String firstNameENG; // Required: First name in English
    private String fatherNameAMH; // Required: Father's name in Amharic
    private String fatherNameENG; // Required: Father's name in English
    private String grandfatherNameAMH; // Required: Grandfather's name in Amharic
    private String grandfatherNameENG; // Required: Grandfather's name in English
    private String motherNameAMH; // Required: Mother's name in Amharic
    private String motherNameENG; // Required: Mother's name in English
    private String motherFatherNameAMH; // Required: Mother's father name in Amharic
    private String motherFatherNameENG; // Required: Mother's father name in English

    // Demographic Information
    private Gender gender; // Required: Gender (MALE, FEMALE, OTHER)
    private Integer age; // Required: Age of the student
    private String phoneNumber; // Required: Unique phone number
    private String dateOfBirthEC; // Required: Date of birth in Ethiopian Calendar
    private LocalDate dateOfBirthGC; // Required: Date of birth in Gregorian Calendar

    // Place of Birth (Foreign Key Codes)
    private String placeOfBirthWoredaCode; // Required: Woreda code for place of birth
    private String placeOfBirthZoneCode; // Required: Zone code for place of birth
    private String placeOfBirthRegionCode; // Required: Region code for place of birth

    // Current Address (Foreign Key Codes)
    private String currentAddressWoredaCode; // Required: Woreda code for current address
    private String currentAddressZoneCode; // Required: Zone code for current address
    private String currentAddressRegionCode; // Required: Region code for current address

    // Additional Personal Information
    private String email; // Optional: Email address
    private MaritalStatus maritalStatus; // Required: Marital status (SINGLE, MARRIED, etc.)
    private String impairmentCode; // Optional: Impairment code if applicable
    private Long schoolBackgroundId; // Required: School background ID

    // Emergency Contact Information
    private String contactPersonFirstNameAMH; // Required: Emergency contact first name in Amharic
    private String contactPersonFirstNameENG; // Required: Emergency contact first name in English
    private String contactPersonLastNameAMH; // Required: Emergency contact last name in Amharic
    private String contactPersonLastNameENG; // Required: Emergency contact last name in English
    private String contactPersonPhoneNumber; // Required: Emergency contact phone number
    private String contactPersonRelation; // Optional: Relation to student (e.g., Parent)

    // Academic Information
    private String dateEnrolledEC; // Required: Enrollment date in Ethiopian Calendar
    private LocalDate dateEnrolledGC; // Required: Enrollment date in Gregorian Calendar
    private String academicYearCode;
    private Long batchClassYearSemesterId; // Required: BatchClassYearSemester ID
    private Long studentRecentStatusId; // Required: Current student status ID
    private Long departmentEnrolledId; // Required: Department enrolled ID
    private String programModalityCode; // Required: Program modality code (e.g., Regular)

    // Document Information
    private DocumentStatus documentStatus; // Optional: Defaults to INCOMPLETE
    private String remark; // Optional: Notes for incomplete documents

    // Transfer and Exit Exam Information
    private Boolean isTransfer; // Optional: Indicates if student is a transfer
    private String exitExamUserID; // Optional: Exit exam user ID
    private Double exitExamScore; // Optional: Exit exam score
    private Boolean isStudentPassExitExam; // Optional: Pass/fail status for exit exam

    // Academic Performance
    private Double grade12Result; // Optional: Grade 12 exam result
}