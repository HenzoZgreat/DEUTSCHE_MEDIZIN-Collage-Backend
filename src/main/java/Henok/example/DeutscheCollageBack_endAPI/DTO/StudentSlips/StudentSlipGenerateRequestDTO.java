package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// dto/StudentSlipGenerateRequestDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSlipGenerateRequestDTO {

    private Long batchClassYearSemesterId;

    private Long sourceId; // Regular, Extension, etc.

    private List<StudentCourseAssignment> students;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentCourseAssignment {
        private Long studentId;
        private List<Long> courseIds;

        public void validate() {
            if (studentId == null) throw new IllegalArgumentException("studentId is required");
            if (courseIds == null || courseIds.isEmpty())
                throw new IllegalArgumentException("courseIds cannot be empty for student: " + studentId);
        }
    }

    // Optional: auto-validate on use
    public void validate() {
        if (batchClassYearSemesterId == null || sourceId == null)
            throw new IllegalArgumentException("batchClassYearSemesterId and sourceId are required");
        if (students == null || students.isEmpty())
            throw new IllegalArgumentException("At least one student must be provided");
        students.forEach(StudentCourseAssignment::validate);
    }
}
