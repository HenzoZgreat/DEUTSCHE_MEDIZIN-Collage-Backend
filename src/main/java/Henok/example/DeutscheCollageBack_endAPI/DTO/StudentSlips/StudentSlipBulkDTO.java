package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO for bulk slip generation (multiple students)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSlipBulkDTO {

    private Long batchClassYearSemesterId;

    private Long sourceId;

    // Each student has their own list of courses
    private List<StudentCourseList> students;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentCourseList {
        private Long studentId;
        private List<Long> courseIds;

        public void validate() {
            if (studentId == null) {
                throw new IllegalArgumentException("studentId is required in bulk request");
            }
            if (courseIds == null || courseIds.isEmpty()) {
                throw new IllegalArgumentException("courseIds cannot be empty for student ID: " + studentId);
            }
        }
    }

    public void validate() {
        if (batchClassYearSemesterId == null || sourceId == null) {
            throw new IllegalArgumentException("batchClassYearSemesterId and sourceId are required");
        }
        if (students == null || students.isEmpty()) {
            throw new IllegalArgumentException("Students list cannot be empty");
        }
        students.forEach(StudentCourseList::validate);
    }
}