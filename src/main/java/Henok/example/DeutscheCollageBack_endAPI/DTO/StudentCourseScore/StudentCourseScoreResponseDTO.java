package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCourseScore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseScoreResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private CourseInfo course;
    private BCYSInfo bcys;
    private CourseSourceInfo courseSource;
    private Double score;
    private Boolean isReleased;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseInfo {
        private Long id;
        private String displayName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BCYSInfo {
        private Long id;
        private String displayName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseSourceInfo {
        private Long id;
        private String displayName;
    }
}

