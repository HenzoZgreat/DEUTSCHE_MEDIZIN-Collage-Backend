package Henok.example.DeutscheCollageBack_endAPI.DTO.GeneralManager;

// DTOs used in the controller and service

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Response DTO – contains full profile data (including blobs if needed)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralManagerDetailDTO {
    private Long id;
    private String firstNameAmharic;
    private String lastNameAmharic;
    private String firstNameEnglish;
    private String lastNameEnglish;
    private String email;
    private String phoneNumber;
    private byte[] nationalIdImage;
    private byte[] photograph;
}

