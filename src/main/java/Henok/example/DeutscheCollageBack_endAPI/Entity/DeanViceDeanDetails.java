package Henok.example.DeutscheCollageBack_endAPI.Entity;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Region;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Woreda;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "dean_vice_dean_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeanViceDeanDetails {

    // Auto-generated primary key for the DeanViceDeanDetails table.
    // Why: Provides an independent identity for DeanViceDeanDetails, decoupling it from User's ID.
    // This allows easier management and avoids shared primary key issues in complex queries or relationships.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key reference to the User table for authentication and role-based details.
    // Why: Establishes a one-to-one relationship with User, ensuring each Dean or Vice-Dean has associated user credentials.
    // Nullable=false ensures every DeanViceDeanDetails record must link to a valid User.
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Personal Information (Names in Amharic and English)
    // firstNameAMH: The first name in Amharic script; required for official documentation in Ethiopia.
    @Column(nullable = false)
    private String firstNameAMH;

    // firstNameENG: The first name in English script; used for international communication and system consistency.
    @Column(nullable = false)
    private String firstNameENG;

    // fatherNameAMH: The father's name in Amharic; part of the traditional Ethiopian naming convention.
    @Column(nullable = false)
    private String fatherNameAMH;

    // fatherNameENG: The father's name in English; ensures bilingual support.
    @Column(nullable = false)
    private String fatherNameENG;

    // grandfatherNameAMH: The grandfather's name in Amharic; completes the full name structure in Ethiopian culture.
    @Column(nullable = false)
    private String grandfatherNameAMH;

    // grandfatherNameENG: The grandfather's name in English; for consistency across languages.
    @Column(nullable = false)
    private String grandfatherNameENG;

    // gender: Enum representing male/female/other; required for demographic and administrative purposes.
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // email: Contact email address; optional but recommended for notifications and communication.
    @Column
    private String email;

    // phoneNumber: Primary contact phone number; required and unique for quick identification and contact.
    @Column(nullable = false)
    private String phoneNumber;

    // Place of Residence (Foreign Keys to location entities)
    // woreda: Reference to the Woreda (district) of residence; part of the Ethiopian administrative division.
    // Why: Links to existing Woreda entity for standardized location data.
    @ManyToOne
    @JoinColumn(name = "residence_woreda_code", nullable = false)
    private Woreda residenceWoreda;

    // zone: Reference to the Zone of residence; higher-level administrative division.
    // Why: Ensures consistency with the hierarchical location structure (Woreda -> Zone -> Region).
    @ManyToOne
    @JoinColumn(name = "residence_zone_code", nullable = false)
    private Zone residenceZone;

    // region: Reference to the Region of residence; top-level administrative division.
    // Why: Completes the location hierarchy for accurate addressing and reporting.
    @ManyToOne
    @JoinColumn(name = "residence_region_code", nullable = false)
    private Region residenceRegion;

    // hiredDateGC: Date of hiring in Gregorian Calendar; tracks employment start for tenure and administrative records.
    // Why: LocalDate is used for date-only storage without time zone issues.
    @Column(name = "hired_date_gc", nullable = false)
    private LocalDate hiredDateGC;

    // photo: Binary data for the user's profile photo; optional for visual identification in the system.
    // Why: Stored as MEDIUMBLOB to handle image files up to 16MB; using byte[] for simplicity in JPA.
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] photo;

    // documents: Binary data for supporting documents (e.g., CV, certificates); optional.
    // Why: Lob annotation for large binary objects; allows uploading a single file or zipped archive.
    @Lob
    private byte[] documents;

    // remarks: Free-text field for any additional notes or comments; optional for administrative flexibility.
    private String remarks;

    // Additional best practices fields:
    // title: Academic or professional title (e.g., Dr., Prof.); optional to denote qualifications.
    // Why: Enhances profile completeness and is common in academic management systems.
    @Column
    private String title;

    // active: Boolean flag indicating if the Dean/Vice-Dean is currently active in their role.
    // Why: Best practice for soft deletion or status management without removing records.
    @Column(nullable = false)
    private boolean active = true;
}