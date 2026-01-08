package Henok.example.DeutscheCollageBack_endAPI.migration.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// Simple result class
@Data
@AllArgsConstructor
public class BulkImportResult {
    private int successCount;
    private int failedCount;
    private List<String> failedUsernames;
}