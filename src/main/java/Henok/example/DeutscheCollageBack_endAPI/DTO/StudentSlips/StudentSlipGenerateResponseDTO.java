package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// dto/StudentSlipGenerateResponseDTO.java
@Data
@NoArgsConstructor
public class StudentSlipGenerateResponseDTO {

    private Long batchClassYearSemesterId;
    private Long sourceId;
    private int totalStudents;
    private int successful = 0;
    private int failed = 0;

    private List<SlipResult> results = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class SlipResult {
        private Long studentId;
        private boolean success;
        private String message;
        private int enrolledCount;
    }

    // Helper to add success
    public void addSuccess(Long studentId, int count) {
        successful++;
        results.add(new SlipResult(studentId, true, "Enrolled in " + count + " course(s)", count));
    }

    // Helper to add failure
    public void addFailure(Long studentId, String error) {
        failed++;
        results.add(new SlipResult(studentId, false, error, 0));
        errors.add("Student " + studentId + ": " + error);
    }
}