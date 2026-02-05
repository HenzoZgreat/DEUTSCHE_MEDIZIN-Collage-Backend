package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchClassYearSemesterDTO {

    private Long bcysId;
    private Long batchId;
    private Long classYearId;
    private String semesterId;

    // For update: departments to add or update
    private List<DepartmentUpdateItem> departmentUpdates = new ArrayList<>();

    // For response: current state
    private List<DepartmentCohortInfo> departments = new ArrayList<>();

    private String name;

    // Inner classes for structured input/output

    @Data
    public static class DepartmentUpdateItem {
        private Long departmentId;          // required

        // If these are null → do not change them
        private String entryYearId;         // academic year code
        private LocalDate classStartGC;
        private String classStartEC;
        private LocalDate classEndGC;
        private String classEndEC;

        // Optional: if you want explicit "remove this department"
        private boolean remove = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentCohortInfo {
        private Long departmentId;
        private String departmentName;
        private String departmentCode;
        private String entryYearId;
        private String academicYearEC;
        private String academicYearGC;
        private LocalDate classStartGC;
        private String classStartEC;
        private LocalDate classEndGC;
        private String classEndEC;
    }
}