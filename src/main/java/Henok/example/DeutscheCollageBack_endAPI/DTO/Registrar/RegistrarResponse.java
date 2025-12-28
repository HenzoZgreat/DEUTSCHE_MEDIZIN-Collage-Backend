package Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar;

import lombok.Data;

@Data
public class RegistrarResponse {
    private Long id;
    private String username;
    private String firstNameAmharic;
    private String lastNameAmharic;
    private String firstNameEnglish;
    private String lastNameEnglish;
    private String email;
    private String phoneNumber;
    private boolean hasPhoto;
    private boolean hasNationalId;
    private boolean enabled;
}
