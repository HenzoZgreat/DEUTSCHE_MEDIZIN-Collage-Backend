package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long userId;
    private String username;
    private String role;
    private String fullName;
    private String fullNameAmharic;
    private String photoBase64;     // or use separate /files endpoint if you prefer
    private String email;
    private String phoneNumber;
    private String departmentName;
    private long departmentId;
}
