package Henok.example.DeutscheCollageBack_endAPI.Entity;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.MaritalStatus;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "applied_students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppliedStudent {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false)
    private String motherNameAMH;

    @Column(nullable = false)
    private String motherNameENG;

    @Column(nullable = false)
    private String motherFatherNameAMH;

    @Column(nullable = false)
    private String motherFatherNameENG;

    // Demographic Information
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "date_of_birth_ec", nullable = false)
    private String dateOfBirthEC;

    @Column(name = "date_of_birth_gc", nullable = false)
    private LocalDate dateOfBirthGC;

    // Place of Birth (Foreign Keys)
    @ManyToOne
    @JoinColumn(name = "place_of_birth_woreda", nullable = false)
    private Woreda placeOfBirthWoreda;

    @ManyToOne
    @JoinColumn(name = "place_of_birth_zone", nullable = false)
    private Zone placeOfBirthZone;

    @ManyToOne
    @JoinColumn(name = "place_of_birth_region", nullable = false)
    private Region placeOfBirthRegion;

    // Current Address (Foreign Keys)
    @ManyToOne
    @JoinColumn(name = "current_address_woreda", nullable = false)
    private Woreda currentAddressWoreda;

    @ManyToOne
    @JoinColumn(name = "current_address_zone", nullable = false)
    private Zone currentAddressZone;

    @ManyToOne
    @JoinColumn(name = "current_address_region", nullable = false)
    private Region currentAddressRegion;

    // Additional Personal Information
    @Column
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @ManyToOne
    @JoinColumn(name = "impairment_code")
    private Impairment impairment;

    @ManyToOne
    @JoinColumn(name = "school_background", nullable = false)
    private SchoolBackground schoolBackground;

    @Column
    private byte[] studentPhoto;

    // Emergency Contact Information
    @Column(nullable = false)
    private String contactPersonFirstNameAMH;

    @Column(nullable = false)
    private String contactPersonFirstNameENG;

    @Column(nullable = false)
    private String contactPersonLastNameAMH;

    @Column(nullable = false)
    private String contactPersonLastNameENG;

    @Column(nullable = false)
    private String contactPersonPhoneNumber;

    @Column
    private String contactPersonRelation;

    // Academic Preferences
    @ManyToOne
    @JoinColumn(name = "department_enrolled", nullable = false)
    private Department departmentEnrolled;

    @ManyToOne
    @JoinColumn(name = "program_modality", nullable = false)
    private ProgramModality programModality;

    // Single PDF document for all required files (nullable)
    @Lob
    private byte[] document;
}