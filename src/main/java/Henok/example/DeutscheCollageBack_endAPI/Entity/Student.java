package Henok.example.DeutscheCollageBack_endAPI.Entity;


import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Student {

    @Id
    private String studentID;

    @Column(nullable = false)
    private String firstNameAm;

    @Column(nullable = false)
    private String firstNameEn;

    @Column(nullable = false)
    private String fatherNameAm;

    @Column(nullable = false)
    private String fatherNameEn;

    @Column(nullable = false)
    private String grandFatherNameAm;

    @Column(nullable = false)
    private String grandFatherNameEn;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_code")
    private Country country;

    @ManyToOne(optional = false)
    @JoinColumn(name = "region_code")
    private Region region;

    @ManyToOne(optional = false)
    @JoinColumn(name = "zone_code")
    private Zone zone;

    @ManyToOne(optional = false)
    @JoinColumn(name = "woreda_code")
    private Woreda woreda;

    @ManyToOne(optional = false)
    @JoinColumn(name = "area_type_code")
    private AreaType areaType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "gender_code")
    private Gender gender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "department_code")
    private Department department;

    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private String email; // nullable

    @Column(nullable = false)
    private String password; // will be auto-generated when creating

    @Column(nullable = false)
    private String maritalStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "batch_code")
    private Batch batch;

    @Column(nullable = false)
    private LocalDate dateOfEnrolled;

    @ManyToOne(optional = false)
    @JoinColumn(name = "school_background_code")
    private SchoolBackground schoolBackground;

    @Column
    private String exitExamStudentId; // nullable

    @Column
    private Double exitExamScore;

    @ManyToOne
    @JoinColumn(name = "impairment_code")
    private Impairment impairment;

    @Column(nullable = false)
    private String emergencyFirstName;

    @Column(nullable = false)
    private String emergencyLastName;

    @Column(nullable = false)
    private String relation;

    @Column
    private String homeTelephone;

    @Column
    private String officeTelephone;

    @Column
    private String mobileNumber;

    @Column(nullable = false)
    private boolean currentlyEmployed;

    @Column(nullable = false)
    private String typeOfJob;

    @Column(nullable = false)
    private String employer;

    @Column(nullable = false)
    private String employerTelephone;

    @Column(nullable = false)
    private Integer age;

    @Lob
    private byte[] photo; // stores actual photo as binary

    @ManyToOne
    @JoinColumn(name = "eheece_id")
    private EHEECE_G12 eheeceInfo;
}
