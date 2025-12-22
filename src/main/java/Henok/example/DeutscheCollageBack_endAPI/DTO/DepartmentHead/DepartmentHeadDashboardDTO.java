package Henok.example.DeutscheCollageBack_endAPI.DTO.DepartmentHead;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentHeadDashboardDTO {
    private DepartmentInfo departmentInfo;
    private Summary summary;
    private List<PendingApproval> pendingApprovals;
    private Instructors instructors;
    private Students students;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentInfo {
        private String departmentName;
        private String modality;
        private String level;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private Long totalStudents;
        private Long totalCourses;
        private Long totalTeachers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingApproval {
        private String teacher;
        private Integer assessments;
        private String course;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Instructors {
        private Long totalTeachers;
        private Long unassigned;
        private Long newlyHired;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Students {
        private Long newIntake;
    }
}


