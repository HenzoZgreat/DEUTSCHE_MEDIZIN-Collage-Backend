package Henok.example.DeutscheCollageBack_endAPI.Entity;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Region;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Woreda;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


// DepartmentHeadDetails Entity
@Entity
@Table(name = "department_head_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentHeadDetails {

    // Primary key for the DepartmentHeadDetails table.
    // Why: Provides an independent identity for DepartmentHeadDetails, similar to StudentDetails.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key reference to User table.
    // Why: Establishes a one-to-one relationship with User, linking to the department head's user account.
    // Nullable=false ensures every DepartmentHeadDetails must link to a User.
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Personal Information (Names in Amharic and English)
    // Why: Stores names in both languages for localization and official records, consistent with StudentDetails.
    @Column(nullable = false)
    private String firstNameAMH;

    @Column(nullable = false)
    private String firstNameENG;

    @Column(nullable = false)
    private String fatherNameAMH;

    @Column(nullable = false)
    private String fatherNameENG;

    @Column(nullable = false)
    private String grandfatherNameAMH;  // Assuming "LastName" refers to grandfather name in Ethiopian context.

    @Column(nullable = false)
    private String grandfatherNameENG;

    // Demographic Information
    // Why: Captures basic personal details for identification and records.
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;  // Enum: MALE, FEMALE, OTHER (assuming defined elsewhere like in StudentDetails).

    @Column(nullable = false, unique = true)
    private String phoneNumber;  // Added as it fits for contact information, similar to StudentDetails.

    @Column
    private String email;

    // Employment Information
    // Why: Tracks when the department head was hired.
    @Column(name = "hired_date_gc", nullable = false)
    private LocalDate hiredDateGC;

    @Column(name = "hired_date_ec", nullable = true)  // Optional Ethiopian Calendar date.
    private String hiredDateEC;

    // Department Association
    // Why: Links to the department where the head is assigned.
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false, unique = true)
    private Department department;

    // Media and Documents
    // Why: Stores photo and documents as binary data. Size limits (2MB photo, 5MB docs) should be enforced in service layer.
    @Column(columnDefinition = "MEDIUMBLOB")  // Suitable for images up to ~16MB, but validate size in app.
    private byte[] photo;

    @Lob  // For larger files like documents.
    private byte[] documents;

    // Place of Residence (Foreign Keys)
    // Why: Captures current residence details, similar to address in StudentDetails.
    @ManyToOne
    @JoinColumn(name = "residence_woreda", nullable = false)
    private Woreda residenceWoreda;

    @ManyToOne
    @JoinColumn(name = "residence_zone", nullable = false)
    private Zone residenceZone;

    @ManyToOne
    @JoinColumn(name = "residence_region", nullable = false)
    private Region residenceRegion;

    @Column(nullable = true)
    private String remark;  // Optional Information

    // Additional fields that fit: e.g., active status for the department head role.
    // Why: Allows tracking if the department head is currently active or not.
    @Column(nullable = false)
    private boolean isActive = true;
}