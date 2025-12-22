package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

// TeacherListDTO.java
@Data
@NoArgsConstructor
public class TeacherListDTO {

    private Long teacherId;
    private String fullNameAmharic;
    private String fullNameEnglish;
    private String departmentName;
    private String title;
    private String email;
    private String phoneNumber;
    private int assignedCoursesCount;
    private String photographBase64;  // Base64-encoded image
}
