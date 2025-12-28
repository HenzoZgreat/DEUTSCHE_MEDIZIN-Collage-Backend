package Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar;

import Henok.example.DeutscheCollageBack_endAPI.Enums.ApplicationStatus;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import lombok.Data;

import java.util.List;
import java.util.Map;

// RegistrarDashboardDTO
// Why: Aggregates all dashboard data into a single DTO for efficient transfer.
// Uses nested static classes for related sub-structures (e.g., BCYSEnrollment, StudentAlert) to keep them organized and avoid separate files.
// Properties: Maps for charts (key-value for easy JSON serialization), lists for tables, primitives for cards.
@Data
public class RegistrarDashboardDTO {

    // Cards
    private long totalApplicants;
    private long pendingApplicants;
    private long registeredStudents;
    private long activeStudents;
    private long incompleteDocuments;
    private long totalDepartments;

    // Charts
    private Map<Gender, Long> applicantGenderDistribution;
    private Map<String, Long> enrollmentByDepartment;
    private List<AcademicYearEnrollment> enrollmentTrendsByAcademicYear; // New: trends by academic year
    private Map<String, Double> averageScoresByDepartment;

    // Tables
    private List<RecentApplicantDTO> recentApplicants;
    private List<StudentAlertDTO> lowScoreAlerts;

    // Nested DTOs
    // Why: Simple structure for JSON: academicYearGC (e.g., "2023-2024") and student count
    @Data
    public static class AcademicYearEnrollment {
        private String academicYearGC;
        private long count;
    }

    @Data
    public static class RecentApplicantDTO {
        private Long id;
        private String firstNameENG;
        private ApplicationStatus applicationStatus;
        private String departmentEnrolled;
    }

    @Data
    public static class StudentAlertDTO {
        private Long studentId;
        private String fullName;
        private Double avgScore;
    }
}
