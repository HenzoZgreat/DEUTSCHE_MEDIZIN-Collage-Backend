package Henok.example.DeutscheCollageBack_endAPI.DTO;

import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherCourseAssignment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TeacherCourseAssignmentResponse {

    private Long id;
    private Long courseId;
    private String courseName;
    private Long bcysId;
    private String bcysDisplay;
    private LocalDateTime assignedAt;

    public TeacherCourseAssignmentResponse(TeacherCourseAssignment assignment) {
        this.id = assignment.getId();
        this.courseId = assignment.getCourse().getCID();
        this.courseName = assignment.getCourse().getCTitle();
        this.bcysId = assignment.getBcys().getBcysID();
        this.bcysDisplay = assignment.getBcys().getDisplayName();
        this.assignedAt = assignment.getAssignedAt();
    }
}