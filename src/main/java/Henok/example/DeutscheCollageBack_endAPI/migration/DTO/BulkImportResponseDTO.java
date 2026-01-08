package Henok.example.DeutscheCollageBack_endAPI.migration.DTO;

import java.util.ArrayList;
import java.util.List;

// BulkImportResponseDTO.java
// Custom response DTO to return both successfully imported courses and skipped ones
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResponseDTO {

    // List of successfully created Course entities
    private List<Course> importedCourses = new ArrayList<>();

    // List of courses that failed validation/import, formatted as "Title(Code)"
    // Why: Provides clear feedback to the caller about which records were skipped
    private List<String> failedCourses = new ArrayList<>();
}
