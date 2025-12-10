package Henok.example.DeutscheCollageBack_endAPI.DTO.GradeReport;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy.SimplifiedStudentCopyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for grade report response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeReportDTO {
    // Student Information
    private String idNumber; // username from user table
    private String fullName;
    private String gender;
    private LocalDate birthDateGC;
    
    // Program Information
    private ProgramModalityInfo programModality;
    private ProgramLevelInfo programLevel;
    private DepartmentInfo department;
    private LocalDate dateEnrolledGC;
    private LocalDate dateIssuedGC; // Set to now
    
    // List of simplified student copies for all semesters (without student info)
    private List<SimplifiedStudentCopyDTO> studentCopies;
    
    // Nested DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgramModalityInfo {
        private String id; // modalityCode
        private String name; // modality
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgramLevelInfo {
        private String id; // code
        private String name;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentInfo {
        private Long id; // dptID
        private String name; // deptName
    }
}

