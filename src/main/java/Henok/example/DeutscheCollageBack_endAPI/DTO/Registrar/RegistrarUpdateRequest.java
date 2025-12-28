package Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrarUpdateRequest {
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
