package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for student copy list response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCopyListResponseDTO {
    private List<StudentCopyDTO> studentCopies;
}

