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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "first_name_amharic", nullable = false)
    private String firstNameAmharic;

    @Column(name = "last_name_amharic", nullable = false)
    private String lastNameAmharic;

    @Column(name = "first_name_english", nullable = false)
    private String firstNameEnglish;

    @Column(name = "last_name_english", nullable = false)
    private String lastNameEnglish;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(name = "national_id_image", columnDefinition = "LONGBLOB")
    private byte[] nationalIdImage;

    @Column(name = "photograph", columnDefinition = "LONGBLOB")
    private byte[] photograph;
}