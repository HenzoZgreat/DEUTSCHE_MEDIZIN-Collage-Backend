package Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralManagerRegisterRequest {
    // User fields
    private String username;
    private String password;

    // GeneralManagerDetail fields
    private String firstNameAmharic;
    private String lastNameAmharic;
    private String firstNameEnglish;
    private String lastNameEnglish;
    private String email;
    private String phoneNumber;
}