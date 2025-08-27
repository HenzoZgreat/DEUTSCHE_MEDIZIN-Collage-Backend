package Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import lombok.Data;

@Data
public class UserRegisterRequest {
    private String username;
    private String password;
    private Role role;
}