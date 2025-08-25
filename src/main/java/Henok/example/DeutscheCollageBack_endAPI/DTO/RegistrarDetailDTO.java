package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarDetailDTO {

    private Long userId;
    private String firstNameAmharic;
    private String lastNameAmharic;
    private String firstNameEnglish;
    private String lastNameEnglish;
    private String email;
    private String phoneNumber;
    private String nationalIdImage;
}