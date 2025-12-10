package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for bulk student copy request (multiple students, same classyear and semester).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCopyBulkRequestDTO {
    private String semesterId;
    private Long classYearId;
    private List<Long> studentIds;
}

