package Henok.example.DeutscheCollageBack_endAPI.DTO.DeanAndVice_Dean;

import lombok.Data;

import java.util.Map;

// DeanDashboardDTO
// DTO for Dean's dashboard statistics and charts.
// Aggregates key metrics about students, departments, and academic structures.
// Why: Provides a structured response for the frontend to render dashboard views/charts.
@Data
public class DeanDashboardDTO {

    // Total number of students across all departments.
    // Sourced from StudentDetails count.
    private long totalStudents;

    // Total number of departments in the college.
    // Sourced from Department count.
    private long totalDepartments;

    // Total number of department heads.
    // Sourced from User count with Role.DEPARTMENT_HEAD.
    private long totalDepartmentHeads;

    // Number of active program modalities (e.g., Regular, Extension).
    // Sourced from ProgramModality count (assuming all are active since no active flag).
    private long activeModalities;

    // Number of active program levels (e.g., Degree, Diploma).
    // Sourced from ProgramLevel count where active = true.
    private long activeLevels;

    // Student counts grouped by program level (e.g., "Bachelor's Degree": 500).
    // Why: Helps visualize distribution across educational levels.
    private Map<String, Long> studentsByLevel;

    // Student counts grouped by program modality (e.g., "Regular": 300).
    // Why: Shows breakdown by study mode.
    private Map<String, Long> studentsByModality;

    // Chart data: Student counts per department (department name -> count).
    // Suitable for bar chart.
    private Map<String, Long> studentsPerDepartment;

    // Chart data: Enrollment trends (academic year code -> new enrollments count).
    // Why: Tracks growth/decline over time; use dateEnrolledGC or academicYear for grouping.
    private Map<String, Long> enrollmentTrend;

    // Chart data: Gender distribution (MALE/FEMALE -> count).
    // Suitable for pie chart.
    private Map<String, Long> genderDistribution;

    // Additional metrics (optional expansions):
    // Total teachers (from User with Role.TEACHER).
    private long totalTeachers;

    // Average Grade 12 result (from StudentDetails.grade12Result).
    private double averageGrade12Result;

    // Pass rate for exit exams (percentage of students where isStudentPassExitExam = true).
    private double exitExamPassRate;
}