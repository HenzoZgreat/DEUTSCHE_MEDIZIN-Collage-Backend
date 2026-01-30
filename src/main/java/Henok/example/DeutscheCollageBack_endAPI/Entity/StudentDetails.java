package Henok.example.DeutscheCollageBack_endAPI.Entity;


import Henok.example.DeutscheCollageBack_endAPI.Enums.DocumentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.MaritalStatus;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "student_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetails {

    // New auto-generated primary key for the StudentDetails table.
    // Why: Provides an independent identity for StudentDetails, decoupling it from User's ID.
    // This allows easier management and avoids shared PK issues in some query scenarios.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key reference to User table.
    // Why: Establishes a one-to-one relationship without sharing the PK.
    // Nullable=false ensures every StudentDetails must link to a User.
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Personal Information (Names in Amharic and English)
    @Column(nullable = false)
    private String firstNameAMH;

    @Column(nullable = false)
    private String firstNameENG;

    @Column(nullable = false)
    private String fatherNameAMH;

    @Column(nullable = false)
    private String fatherNameENG;

    @Column(nullable = false)
    private String grandfatherNameAMH;

    @Column(nullable = false)
    private String grandfatherNameENG;

    @Column(nullable = true)
    private String motherNameAMH;

    @Column(nullable = true)
    private String motherNameENG;

    @Column(nullable = true)
    private String motherFatherNameAMH;

    @Column(nullable = true)
    private String motherFatherNameENG;

    // Demographic Information
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = true)
    private Integer age;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(name = "date_of_birth_ec", nullable = true)
    private String dateOfBirthEC;

    @Column(name = "date_of_birth_gc", nullable = true)
    private LocalDate dateOfBirthGC;

    // Place of Birth (Foreign Keys)
    @ManyToOne
    @JoinColumn(name = "place_of_birth_woreda", nullable = true)
    private Woreda placeOfBirthWoreda;

    @ManyToOne
    @JoinColumn(name = "place_of_birth_zone", nullable = true)
    private Zone placeOfBirthZone;

    @ManyToOne
    @JoinColumn(name = "place_of_birth_region", nullable = true)
    private Region placeOfBirthRegion;

    // Current Address (Foreign Keys)
    @ManyToOne
    @JoinColumn(name = "current_address_woreda", nullable = true)
    private Woreda currentAddressWoreda;

    @ManyToOne
    @JoinColumn(name = "current_address_zone", nullable = true)
    private Zone currentAddressZone;

    @ManyToOne
    @JoinColumn(name = "current_address_region", nullable = true)
    private Region currentAddressRegion;

    // Additional Personal Information
    @Column
    private String email;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @ManyToOne
    @JoinColumn(name = "impairment_code")
    private Impairment impairment;

    @ManyToOne
    @JoinColumn(name = "school_background", nullable = false)
    private SchoolBackground schoolBackground;

    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] studentPhoto;

    // Emergency Contact Information
    @Column(nullable = true)
    private String contactPersonFirstNameAMH;

    @Column(nullable = true)
    private String contactPersonFirstNameENG;

    @Column(nullable = true)
    private String contactPersonLastNameAMH;

    @Column(nullable = true)
    private String contactPersonLastNameENG;

    @Column(nullable = true)
    private String contactPersonPhoneNumber;

    @Column
    private String contactPersonRelation;

    // Academic Information
    @Column(name = "date_enrolled_ec", nullable = true)
    private String dateEnrolledEC;

    @Column(name = "date_enrolled_gc", nullable = true)
    private LocalDate dateEnrolledGC;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "academic_year_code",           // column name in student_details table
            referencedColumnName = "yearCode",     // PK column in academic_year table
            nullable = true                       // or true if it can be null
    )
    private AcademicYear academicYear;

    @ManyToOne
    @JoinColumn(name = "batch_class_year_semester", nullable = false)
    private BatchClassYearSemester batchClassYearSemester;

    @ManyToOne
    @JoinColumn(name = "student_recent_status", nullable = false)
    private StudentStatus studentRecentStatus;

    @ManyToOne
    @JoinColumn(name = "department_enrolled", nullable = false)
    private Department departmentEnrolled;

    @ManyToOne
    @JoinColumn(name = "program_modality", nullable = false)
    private ProgramModality programModality;

    // --------- New Added -------------
    @Column(nullable = true, precision = 4, scale = 2)
    private BigDecimal cgpa;                // can be null if no grades yet

    @Column(nullable = true)
    private Integer totalEarnedCreditHours; // null or 0 if no passed courses

    @Column(nullable = true)
    private Integer totalNumberOfCoursesTaken;
    //----------------------------------

    // Single PDF document for all required files (nullable)
    @Lob
    private byte[] document;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentStatus documentStatus = DocumentStatus.INCOMPLETE; // Default to INCOMPLETE

    @Column
    private Double grade12Result;

    // Remark for incomplete documents
    private String remark;

    // Transfer and Exit Exam Information
    @Column(nullable = false)
    private boolean isTransfer;

    @Column
    private String exitExamUserID;

    @Column
    private Double exitExamScore;

    @Column(nullable = false)
    private boolean isStudentPassExitExam;

    public boolean isStudentPassExitExam() {
        return isStudentPassExitExam;
    }

    public boolean isTransfer() {
        return isTransfer;
    }
}