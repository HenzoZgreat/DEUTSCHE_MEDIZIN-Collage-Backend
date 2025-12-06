package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// SlipCourseDTO.java
// Purpose: Course details for slip display
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlipCourseDTO {
    private Long courseId;
    private String code;
    private String title;
    private Integer lectureHours;
    private Integer labHours;
    private Integer totalHours;
}