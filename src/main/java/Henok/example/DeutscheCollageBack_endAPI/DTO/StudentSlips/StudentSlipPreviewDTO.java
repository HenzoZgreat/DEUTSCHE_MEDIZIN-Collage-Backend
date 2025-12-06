package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


// StudentSlipPreviewDTO.java
// Purpose: Full slip preview data for one student
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSlipPreviewDTO {

    private Long studentId;
    private String username;
    private String fullNameEng;        // First + Father + Grandfather (English)
    private String fullNameAmh;        // Optional: Amharic version
    private Integer age;
    private Gender gender;

    // Academic Context
    private String departmentId;
    private String departmentName;
    private String departmentCode;

    private Long classYearId;
    private String classYearName;

    private String semesterId;
    private String semesterName;

    private String academicYearCode;
    private String academicYearGC;
    private String academicYearEC;

    // Enrollment Type = Program Modality
    private String enrollmentTypeCode;
    private String enrollmentTypeName;

    // Selected Courses
    private List<SlipCourseDTO> courses;

    // Optional: BCYS Display Name (e.g., "2024-Regular-Year3-Sem2")
    private String batchDisplayName;
}