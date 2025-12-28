package Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralManagerRegisterRequest {
    // User fields
    @Size(max = 50)
    private String username;

    @Size(min = 6, max = 128)
    private String password;

    // GeneralManagerDetail fields
    @Size(max = 100)
    private String firstNameAmharic;

    @Size(max = 100)
    private String lastNameAmharic;

    @Size(max = 100)
    private String firstNameEnglish;

    @Size(max = 100)
    private String lastNameEnglish;

    @Size(max = 100)
    private String email;

    @Size(max = 15)
    private String phoneNumber;
}