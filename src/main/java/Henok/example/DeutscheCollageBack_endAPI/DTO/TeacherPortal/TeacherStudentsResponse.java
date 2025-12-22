package Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// TeacherStudentsResponse (DTO with nested class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherStudentsResponse {

    private String message = "Students retrieved successfully";

    private int totalStudents = 0;

    private List<StudentInfo> students = new ArrayList<>();

    // Nested DTO – no separate file needed
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentInfo {
        private Long studentId;              // from StudentDetails.id
        private String studentIdNumber;      // institutional ID
        private String fullNameENG;
        private String fullNameAMH;
        private String email;
        private String phoneNumber;
        private String department;
        private String program;
    }
}