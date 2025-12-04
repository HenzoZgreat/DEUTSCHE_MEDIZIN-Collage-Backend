package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips;

import lombok.Data;

@Data
public class StudentSlipDTO {
    private Long studentId;
    private String username;

    private String fullNameAMH;   // e.g. "አበበ ከበደ ደሴ"
    private String fullNameENG;   // e.g. "Abebe Kebede Desse"

    private Long bcysId;
    private String bcysDisplayName; // e.g. "2024-1-1"

    private Long departmentId;
    private String departmentName;

    private String programModalityCode;
    private String programModalityName;

    private String programLevelCode;
    private String programLevelName;
}