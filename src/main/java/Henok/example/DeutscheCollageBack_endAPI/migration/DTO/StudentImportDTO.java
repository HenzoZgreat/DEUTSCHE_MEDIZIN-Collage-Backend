package Henok.example.DeutscheCollageBack_endAPI.migration.DTO;

import lombok.Data;

/**
 * DTO used for the one-time bulk import of legacy students.
 *
 * Design decisions:
 * - All fields are String (or primitive wrappers) to handle CSV/JSON parsing safely.
 * - Foreign key references that come as numeric IDs from the legacy data are kept as String
 *   (departmentEnrolledId, batchClassYearSemesterId, schoolBackgroundId, studentRecentStatusId).
 *   These will be parsed to Long in the import service and used to fetch the entities directly by ID.
 * - Text columns like "Batch" / "Recent_Batch" are ignored since you confirmed there is no description field.
 * - Mother names, current address, email, impairment, academicYear, photo, document etc. are not present → will be null.
 * - Password will be defaulted to "stud1234" in the import service.
 * - Phone number is kept as String because legacy values are formatted (e.g., "(+251) ...").
 * - Empty or blank values in CSV are tolerated – mapping logic will skip or set defaults.
 */
@Data
public class StudentImportDTO {

    // User fields
    private String username;                   // "username/student-id" → e.g. DHMC-MD-02-12
    private String password;                   // usually empty → default "stud1234"

    // Personal names – English
    private String firstNameENG;
    private String fatherNameENG;
    private String grandfatherNameENG;

    // Personal names – Amharic
    private String firstNameAMH;
    private String fatherNameAMH;
    private String grandfatherNameAMH;

    // Mother names – not present in legacy data → will remain null
    private String motherNameAMH;
    private String motherNameENG;
    private String motherFatherNameAMH;
    private String motherFatherNameENG;

    // Demographic
    private String gender;                     // "Male" or "Female" → mapped to Gender enum
    private String dateOfBirthGC;               // YYYY-MM-DD → used for age calculation and dateOfBirthGC
    private String maritalStatus;              // "Single", "Married", ... → mapped to MaritalStatus enum
    private String phoneNumber;                 // formatted string, unique in DB

    // Enrollment
    private String dateEnrolledGC;              // YYYY-MM-DD

    // Foreign keys provided as numeric IDs (Long in DB)
    private String departmentEnrolledId;        // e.g. "2" → Department.id
    private String batchClassYearSemesterId;    // e.g. "15" → BatchClassYearSemester.id
    private String studentRecentStatusId;      // e.g. "2" → StudentStatus.id
    private String schoolBackgroundId;         // e.g. "1" → SchoolBackground.id

    // Program modality – code provided
    private String programModalityCode;        // e.g. "RG"

    // Birth place – codes provided
    private String placeOfBirthRegionCode;     // e.g. "AMH"
    private String placeOfBirthZoneCode;       // e.g. "GOJW"
    private String placeOfBirthWoredaCode;     // e.g. "1199"

    // Emergency contact – mostly empty in sample
    private String contactPersonFirstNameENG;
    private String contactPersonPhoneNumber;
    private String contactPersonRelation;

    // Other useful legacy fields
    private String remark;
    private String isTransfer;                 // "TRUE"/"FALSE"
    private String documentStatus;             // "TRUE" → COMPLETE, else INCOMPLETE
    private String exitExamUserID;
    private String exitExamScore;              // will be parsed to Double if present
    private String isStudentPassExitExam;      // "TRUE"/"FALSE"
    private String grade12Result;              // will be parsed to Double if present
}
