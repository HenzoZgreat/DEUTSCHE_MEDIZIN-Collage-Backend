package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.GradeReport.GradeReportDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.GradeReport.GradeReportRequestDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.GradeReport.GradeReportResponseDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy.SimplifiedStudentCopyDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy.StudentCopyRequestDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramLevel;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Repository.BatchClassYearSemesterRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.StudentCourseScoreRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.StudentDetailsRepository;
import Henok.example.DeutscheCollageBack_endAPI.Service.Utility.ClassYearSemesterOrderingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GradeReportService {

    @Autowired
    private StudentDetailsRepository studentDetailsRepository;

    @Autowired
    private StudentCourseScoreRepo studentCourseScoreRepo;

    @Autowired
    private BatchClassYearSemesterRepo batchClassYearSemesterRepo;

    @Autowired
    private StudentCopyService studentCopyService;

    @Autowired
    private ClassYearSemesterOrderingService orderingService;

    /**
     * Generates grade reports for multiple students.
     * Each grade report contains student information and student copies for all semesters they have taken.
     * 
     * @param request The request containing list of student IDs
     * @return GradeReportResponseDTO containing grade reports for all valid students
     */
    @Transactional(readOnly = true)
    public GradeReportResponseDTO generateGradeReports(GradeReportRequestDTO request) {
        if (request == null || request.getStudentIds() == null || request.getStudentIds().isEmpty()) {
            throw new IllegalArgumentException("Student IDs list cannot be null or empty");
        }

        List<GradeReportDTO> gradeReports = new ArrayList<>();

        for (Long studentId : request.getStudentIds()) {
            if (studentId == null) {
                continue; // Skip null student IDs
            }

            try {
                System.out.println("Generating grade report for student ID: " + studentId);
                GradeReportDTO gradeReport = generateGradeReportForStudent(studentId);
                if (gradeReport != null) {
                    gradeReports.add(gradeReport);
                }
                System.out.println("Successfully generated grade report for student ID: " + studentId + " and\n the report: " + gradeReport);
            } catch (IllegalArgumentException e) {
                // Skip students with invalid data, continue with others
                continue;
            } catch (Exception e) {
                // Skip students that don't exist or have errors
                // Log error if needed, but continue with other students
                continue;
            }
        }

        GradeReportResponseDTO response = new GradeReportResponseDTO();
        System.out.println("Setting Grade-Reports ...");
        response.setGradeReports(gradeReports);
        System.out.println("Successfully set Grade-Reports: \n" + gradeReports);
        return response;
    }

    /**
     * Generates a grade report for a single student.
     * 
     * @param studentId The student ID
     * @return GradeReportDTO or null if student doesn't exist or has no records
     */
    private GradeReportDTO generateGradeReportForStudent(Long studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }

        // Get student details
        StudentDetails student = studentDetailsRepository.findById(studentId).orElse(null);
        if (student == null) {
            return null; // Skip if student doesn't exist
        }

        if (student.getUser() == null) {
            return null; // Skip if student has no user account
        }

        // Get all released course scores for the student
        List<StudentCourseScore> allReleasedScores;
        try {
            allReleasedScores = studentCourseScoreRepo.findByStudentAndIsReleasedTrue(student.getUser());
        } catch (Exception e) {
            // If error fetching scores, return null to skip this student
            return null;
        }

        if (allReleasedScores == null || allReleasedScores.isEmpty()) {
            return null; // Skip if student has no released scores
        }

        // Get unique ClassYear + Semester combinations from the released scores
        Map<String, ClassYearSemesterPair> uniqueCombinations = new HashMap<>();
        for (StudentCourseScore score : allReleasedScores) {
            BatchClassYearSemester bcys = score.getBatchClassYearSemester();
            ClassYear classYear = bcys.getClassYear();
            Semester semester = bcys.getSemester();
            
            String key = classYear.getId() + "_" + semester.getAcademicPeriodCode();
            if (!uniqueCombinations.containsKey(key)) {
                uniqueCombinations.put(key, new ClassYearSemesterPair(classYear, semester));
            }
        }

        // Generate simplified student copies for each unique combination
        List<SimplifiedStudentCopyDTO> studentCopies = new ArrayList<>();
        Batch studentBatch = student.getBatchClassYearSemester().getBatch();

        for (ClassYearSemesterPair pair : uniqueCombinations.values()) {
            try {
                // Find BatchClassYearSemester for student's batch + this classYear + semester
                BatchClassYearSemester bcys = batchClassYearSemesterRepo
                        .findByBatchAndClassYearAndSemester(studentBatch, pair.classYear, pair.semester)
                        .orElse(null);

                if (bcys != null) {
                    // Generate simplified student copy for this semester (without student info)
                    StudentCopyRequestDTO copyRequest = new StudentCopyRequestDTO();
                    copyRequest.setStudentId(studentId);
                    copyRequest.setClassYearId(pair.classYear.getId());
                    copyRequest.setSemesterId(pair.semester.getAcademicPeriodCode());

                    SimplifiedStudentCopyDTO studentCopy = studentCopyService.generateSimplifiedStudentCopy(copyRequest);
                    studentCopies.add(studentCopy);
                }
            } catch (Exception e) {
                // Skip this semester if there's an error generating the copy
                // Log error if needed, but continue with other semesters
                continue;
            }
        }

        // Sort student copies by ClassYear then Semester
        studentCopies.sort((sc1, sc2) -> {
            try {
                // Compare by ClassYear first
                int classYearCompare = orderingService.compareClassYearStrings(
                        sc1.getClassyear().getName(),
                        sc2.getClassyear().getName()
                );
                if (classYearCompare != 0) {
                    return classYearCompare;
                }
                // If same ClassYear, compare by Semester
                return orderingService.compareSemesterStrings(
                        sc1.getSemester().getId(),
                        sc2.getSemester().getId()
                );
            } catch (Exception e) {
                // If comparison fails, maintain current order
                return 0;
            }
        });

        // Build GradeReportDTO
        GradeReportDTO gradeReport = new GradeReportDTO();
        
        // Student Information
        gradeReport.setIdNumber(student.getUser().getUsername());
        gradeReport.setFullName(String.join(" ",
                student.getFirstNameENG(),
                student.getFatherNameENG(),
                student.getGrandfatherNameENG()).trim());
        gradeReport.setGender(student.getGender().name());
        gradeReport.setBirthDateGC(student.getDateOfBirthGC());

        // Program Information
        GradeReportDTO.ProgramModalityInfo programModalityInfo = new GradeReportDTO.ProgramModalityInfo();
        programModalityInfo.setId(student.getProgramModality().getModalityCode());
        programModalityInfo.setName(student.getProgramModality().getModality());
        gradeReport.setProgramModality(programModalityInfo);

        Department department = student.getDepartmentEnrolled();
        GradeReportDTO.ProgramLevelInfo programLevelInfo = new GradeReportDTO.ProgramLevelInfo();
        ProgramLevel programLevel = department.getProgramLevel();
        if (programLevel != null) {
            programLevelInfo.setId(programLevel.getCode());
            programLevelInfo.setName(programLevel.getName());
        }
        gradeReport.setProgramLevel(programLevelInfo);

        GradeReportDTO.DepartmentInfo departmentInfo = new GradeReportDTO.DepartmentInfo();
        departmentInfo.setId(department.getDptID());
        departmentInfo.setName(department.getDeptName());
        gradeReport.setDepartment(departmentInfo);

        gradeReport.setDateEnrolledGC(student.getDateEnrolledGC());
        gradeReport.setDateIssuedGC(LocalDate.now());

        // Student Copies
        gradeReport.setStudentCopies(studentCopies);

        return gradeReport;
    }

    /**
     * Helper class to hold ClassYear and Semester pairs.
     */
    private static class ClassYearSemesterPair {
        ClassYear classYear;
        Semester semester;

        ClassYearSemesterPair(ClassYear classYear, Semester semester) {
            this.classYear = classYear;
            this.semester = semester;
        }
    }
}

