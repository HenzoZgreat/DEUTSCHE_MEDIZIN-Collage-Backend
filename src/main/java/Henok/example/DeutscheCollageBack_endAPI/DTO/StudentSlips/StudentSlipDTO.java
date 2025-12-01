package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO for single student slip (multiple courses)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSlipDTO {

    private Long studentId;

    private Long batchClassYearSemesterId;  // BCYS ID

    private Long sourceId;                  // e.g., Regular, Extension, etc.

    private List<Long> courseIds;           // List of course IDs to enroll in

    // Optional: useful for frontend
    public void validate() {
        if (studentId == null || batchClassYearSemesterId == null || sourceId == null) {
            throw new IllegalArgumentException("studentId, batchClassYearSemesterId and sourceId are required");
        }
        if (courseIds == null || courseIds.isEmpty()) {
            throw new IllegalArgumentException("At least one courseId must be provided");
        }
    }
}
