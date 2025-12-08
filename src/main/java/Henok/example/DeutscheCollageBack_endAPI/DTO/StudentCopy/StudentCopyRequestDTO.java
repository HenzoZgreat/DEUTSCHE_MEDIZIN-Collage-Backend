package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for student copy request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCopyRequestDTO {
    private Long studentId; // StudentDetails ID or User ID
    private Long classYearId;
    private String semesterId; // Semester ID (academicPeriodCode)
}

