package Henok.example.DeutscheCollageBack_endAPI.DTO.DepartmentHead;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentTeacherDTO {
    private Long teacherId;
    private String fullName;
    private String title;
    private String email;
    private String phoneNumber;
    private Integer yearsOfExperience;
    private Long numberOfCourses;
}


