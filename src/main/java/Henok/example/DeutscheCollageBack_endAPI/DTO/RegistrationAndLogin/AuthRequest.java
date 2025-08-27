package Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}