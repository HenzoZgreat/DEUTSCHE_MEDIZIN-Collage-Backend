package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "general_manager_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralManagerDetail {

    // Primary Key and User Relationship
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Personal Information (Names in Amharic and English)
    @Column(name = "first_name_amharic", nullable = false)
    private String firstNameAmharic;

    @Column(name = "last_name_amharic", nullable = false)
    private String lastNameAmharic;

    @Column(name = "first_name_english", nullable = false)
    private String firstNameEnglish;

    @Column(name = "last_name_english", nullable = false)
    private String lastNameEnglish;

    // Contact Information
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    // Identity Verification
    @Column(name = "national_id_image", columnDefinition = "LONGBLOB")
    private byte[] nationalIdImage;

    // Photograph
    @Column(name = "photograph", columnDefinition = "LONGBLOB")
    private byte[] photograph;
}