package Henok.example.DeutscheCollageBack_endAPI.DTO.GeneralManager;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request DTO for partial updates – all fields optional (validation only applies if provided)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralManagerUpdateDTO {

    @Size(min = 2, max = 50, message = "First name (Amharic) must be between 2 and 50 characters")
    private String firstNameAmharic;

    @Size(min = 2, max = 50)
    private String lastNameAmharic;

    @Size(min = 2, max = 50)
    private String firstNameEnglish;

    @Size(min = 2, max = 50)
    private String lastNameEnglish;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private byte[] nationalIdImage;
    private byte[] photograph;
}
