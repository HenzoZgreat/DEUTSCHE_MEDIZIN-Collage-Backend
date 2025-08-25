package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "registrar_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
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

    @Column
    private String phoneNumber;

    @Column(name = "national_id_image", columnDefinition = "LONGBLOB")
    private byte[] nationalIdImage;
}