package Henok.example.DeutscheCollageBack_endAPI.DTO.GeneralManager;

// GeneralManagerDashboardDTO – Main response DTO for the dashboard endpoint.
// Why nested static classes: Keeps all related DTOs in one file, avoids package clutter,
// improves readability (all dashboard structures in one place), and follows clean code practice for tightly coupled data structures.

import Henok.example.DeutscheCollageBack_endAPI.Enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralManagerDashboardDTO {

    private StudentOverview studentOverview;
    private ApplicationOverview applicationOverview;
    private StaffOverview staffOverview;
    private DepartmentOverview departmentOverview;
    private OperationalAlerts operationalAlerts;
    private Trends trends;

    // Nested DTOs for student section
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentOverview {
        private long totalEnrolled;
        private List<DepartmentCount> byDepartment;
        private List<ProgramModalityCount> byProgramModality;
        private List<StudentStatusCount> byStatus;
        private long incompleteDocuments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentCount {
        private String departmentName;     // Name of the department
        private long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgramModalityCount {
        private String modality;           // e.g., Regular, Extension
        private long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentStatusCount {
        private String status;             // e.g., ACTIVE, WITHDRAWN, GRADUATED
        private long count;
    }

    // Nested DTOs for applications
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicationOverview {
        private long totalApplied;
        private long pendingCount;         // Cached for quick alert access
        private List<ApplicationStatusCount> byStatus;
        private List<DepartmentCount> byDepartment;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicationStatusCount {
        private ApplicationStatus status;  // PENDING, APPROVED, REJECTED
        private long count;
    }

    // Nested DTOs for staff
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffOverview {
        private long totalTeachers;
        private long totalRegistrars;
        private long totalDepartmentHeads;
        private long totalDeansViceDeans;
        private long totalStaff;           // Sum of all roles
    }

    // Nested DTOs for departments
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentOverview {
        private long totalDepartments;
        // Can be extended later with avg students/staff per dept, low-staff alerts, etc.
    }

    // Nested DTOs for alerts
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationalAlerts {
        private long pendingApplications;
        private long studentsWithImpairments;
        // Additional alerts can be added here (e.g., overdue fees, system issues)
    }

    // Nested DTOs for trends
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Trends {
        private List<EnrollmentTrend> enrollmentOverYears;
        // Future: applicationTrend, staffHireTrend, etc.
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentTrend {
        private String academicYear;       // Format: "2023-2024"
        private long count;
    }
}

