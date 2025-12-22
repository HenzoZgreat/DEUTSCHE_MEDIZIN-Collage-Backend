package Henok.example.DeutscheCollageBack_endAPI.DTO.Passwords;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest_NewPasswordOnly {
    private String newPassword;
}