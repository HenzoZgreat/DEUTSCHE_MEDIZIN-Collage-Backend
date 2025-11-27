package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseScoreResponseDTO {
    private Long studentId;
    private CourseInfo course;
    private BCYSInfo brys;
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

