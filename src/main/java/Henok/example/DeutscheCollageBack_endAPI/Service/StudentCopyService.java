package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy.CourseGradeDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy.SimplifiedStudentCopyDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy.StudentCopyDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCopy.StudentCopyRequestDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramLevel;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.AcademicYearRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.SemesterRepo;
import Henok.example.DeutscheCollageBack_endAPI.Service.Utility.AcademicYearUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentCopyService {

    @Autowired
    private StudentDetailsRepository studentDetailsRepository;

    @Autowired
    private BatchClassYearSemesterRepo batchClassYearSemesterRepo;

    @Autowired
    private ClassYearRepository classYearRepository;

    @Autowired
    private SemesterRepo semesterRepo;

    @Autowired
    private StudentCourseScoreRepo studentCourseScoreRepo;

    @Autowired
    private GradingSystemService gradingSystemService;

    @Autowired
    private AcademicYearRepo academicYearRepo;

    @Autowired
    private AcademicYearUtilityService academicYearUtilityService;

    private static final double MINIMUM_PASSING_GPA = 2.0; // Minimum GPA to pass a semester

    /**
     * Generates a student copy (transcript) for a specific classyear and semester.
     * 
     * @param request The request containing studentId, classYearId, and semesterId
     * @return StudentCopyDTO containing all student information and course grades
     * @throws ResourceNotFoundException if student, classyear, semester, or batch-class-year-semester not found
     */
    @Transactional(readOnly = true)
    public StudentCopyDTO generateStudentCopy(StudentCopyRequestDTO request) {
        // 1. Get student details
        StudentDetails student = studentDetailsRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));
        System.out.println("passed StudentDetails");

        // 2. Get requested classyear and semester
        ClassYear classYear = classYearRepository.findById(request.getClassYearId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found with id: " + request.getClassYearId()));
        System.out.println("passed ClassYear");

        Semester semester = semesterRepo.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id: " + request.getSemesterId()));
        System.out.println("passed Semester");

        // 3. Find BatchClassYearSemester matching student's batch + requested classyear + requested semester
        BatchClassYearSemester batchClassYearSemester = batchClassYearSemesterRepo
                .findByBatchAndClassYearAndSemester(
                        student.getBatchClassYearSemester().getBatch(),
                        classYear,
                        semester
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BatchClassYearSemester not found for batch: " + 
                        student.getBatchClassYearSemester().getBatch().getBatchName() +
                        ", classYear: " + classYear.getClassYear() +
                        ", semester: " + semester.getAcademicPeriodCode()
                ));
        System.out.println("passed BCYS");

        // 4. Get all courses for this student and batchClassYearSemester
        List<StudentCourseScore> courseScores = studentCourseScoreRepo
                .findByStudentAndBatchClassYearSemester(student.getUser(), batchClassYearSemester);

        System.out.println("all courses found successfully : ");

        // 5. Get grading system for student's department
        Department department = student.getDepartmentEnrolled();
        GradingSystem gradingSystem = gradingSystemService.findApplicableGradingSystem(department);

        System.out.println("Grading system choosen : ");

        // 6. Build course grades list
        System.out.println("Iterating through the courses ... ");
        List<CourseGradeDTO> courseGrades = new ArrayList<>();
        for (StudentCourseScore score : courseScores) {
            if (score.getScore() == null || !score.isReleased()) {
                System.out.println("\t" + score.getCourse().getCTitle() + " --------- null or not released");
                continue; // Skip courses without scores or not released
            }

            Course course = score.getCourse();
            int totalCrHrs = course.getTheoryHrs() + course.getLabHrs();

            // Find matching mark interval for the score
            MarkInterval interval = gradingSystem.getIntervals().stream()
                    .filter(i -> score.getScore() >= i.getMin() && score.getScore() <= i.getMax())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No matching interval for score: " + score.getScore() + 
                            " in grading system: " + gradingSystem.getVersionName()
                    ));

            String letterGrade = interval.getGradeLetter();
            Double gradePoint = totalCrHrs * interval.getGivenValue();

            CourseGradeDTO courseGrade = new CourseGradeDTO();
            courseGrade.setCourseCode(course.getCCode());
            courseGrade.setCourseTitle(course.getCTitle());
            courseGrade.setTotalCrHrs(totalCrHrs);
            courseGrade.setLetterGrade(letterGrade);
            courseGrade.setGradePoint(gradePoint);

            courseGrades.add(courseGrade);

            System.out.println("\t" + score.getCourse().getCTitle() + " ------- " + score.getScore() + " " + letterGrade);
        }

        // 7. Calculate Semester GPA
        System.out.println("Calculating Semester Gpa ... ");
        double semesterGPA = calculateGPA(courseGrades);
        System.out.println(semesterGPA);

        // 8. Calculate Semester CGPA (cumulative from enrollment until requested semester)
        System.out.println("Calulating CGPA ... ");
        double semesterCGPA = calculateCGPA(student.getUser(), batchClassYearSemester, gradingSystem);
        System.out.println(semesterCGPA);

        // 9. Determine status
        String status = semesterGPA >= MINIMUM_PASSING_GPA ? "PASSED" : "FAILED";

        // 10. Find AcademicYear
        AcademicYear academicYear = batchClassYearSemester.getEntryYear();

        // 11. Build response DTO
        StudentCopyDTO dto = new StudentCopyDTO();
        
        // Student Information
        dto.setIdNumber(student.getUser().getUsername());
        dto.setFullName(String.join(" ",
                student.getFirstNameENG(),
                student.getFatherNameENG(),
                student.getGrandfatherNameENG()).trim());
        dto.setGender(student.getGender().name());

        // Program Information
        StudentCopyDTO.ProgramModalityInfo programModalityInfo = new StudentCopyDTO.ProgramModalityInfo();
        programModalityInfo.setId(student.getProgramModality().getModalityCode());
        programModalityInfo.setName(student.getProgramModality().getModality());
        dto.setProgramModality(programModalityInfo);

        StudentCopyDTO.ProgramLevelInfo programLevelInfo = new StudentCopyDTO.ProgramLevelInfo();
        ProgramLevel programLevel = student.getDepartmentEnrolled().getProgramLevel();
        if (programLevel != null) {
            programLevelInfo.setId(programLevel.getCode());
            programLevelInfo.setName(programLevel.getName());
        }
        dto.setProgramLevel(programLevelInfo);

        dto.setDateEnrolledGC(student.getDateEnrolledGC());

        StudentCopyDTO.DepartmentInfo departmentInfo = new StudentCopyDTO.DepartmentInfo();
        departmentInfo.setId(department.getDptID());
        departmentInfo.setName(department.getDeptName());
        dto.setDepartment(departmentInfo);

        dto.setDateOfBirthGC(student.getDateOfBirthGC());

        // Academic Context
        StudentCopyDTO.ClassYearInfo classYearInfo = new StudentCopyDTO.ClassYearInfo();
        classYearInfo.setId(classYear.getId());
        classYearInfo.setName(classYear.getClassYear());
        dto.setClassyear(classYearInfo);

        StudentCopyDTO.SemesterInfo semesterInfo = new StudentCopyDTO.SemesterInfo();
        semesterInfo.setId(semester.getAcademicPeriodCode());
        semesterInfo.setName(semester.getAcademicPeriod());
        dto.setSemester(semesterInfo);

        if (academicYear != null) {
            StudentCopyDTO.AcademicYearInfo academicYearInfo = new StudentCopyDTO.AcademicYearInfo();
            academicYearInfo.setYearCode(academicYear.getYearCode());
            academicYearInfo.setYearGC(academicYear.getAcademicYearGC());
            dto.setAcademicYear(academicYearInfo);
        }

        // Course Grades
        dto.setCourses(courseGrades);

        // GPA Information
        dto.setSemesterGPA(semesterGPA);
        dto.setSemesterCGPA(semesterCGPA);
        dto.setStatus(status);

        return dto;
    }

    /**
     * Calculates GPA for a list of course grades.
     * Formula: Sum of (GradePoint) / Sum of (Credit Hours)
     */
    public double calculateGPA(List<CourseGradeDTO> courseGrades) {
        if (courseGrades == null || courseGrades.isEmpty()) {
            return 0.0;
        }

        double totalGradePoints = courseGrades.stream()
                .mapToDouble(CourseGradeDTO::getGradePoint)
                .sum();

        int totalCreditHours = courseGrades.stream()
                .mapToInt(CourseGradeDTO::getTotalCrHrs)
                .sum();

        if (totalCreditHours == 0) {
            return 0.0;
        }

        return totalGradePoints / totalCreditHours;
    }

    /**
     * Calculates CGPA (Cumulative GPA) from enrollment until the requested semester.
     * Includes all courses from all semesters up to and including the requested semester.
     */
    public double calculateCGPA(User student, BatchClassYearSemester requestedBCYS, GradingSystem gradingSystem) {
        // Get all released course scores for the student, ordered by classStart_GC
        List<StudentCourseScore> allScores = studentCourseScoreRepo
                .findByStudentAndIsReleasedTrueOrderedByClassStart(student);

        if (allScores.isEmpty()) {
            return 0.0;
        }

        // Filter to include only courses up to and including the requested semester
        // Compare by classStart_GC date
        LocalDate requestedDate = requestedBCYS.getClassStart_GC();
        if (requestedDate == null) {
            // If requested BCYS doesn't have a date, include all courses
            requestedDate = allScores.stream()
                    .map(sc -> sc.getBatchClassYearSemester().getClassStart_GC())
                    .filter(date -> date != null)
                    .max(Comparator.naturalOrder())
                    .orElse(null);
        }

        final LocalDate finalRequestedDate = requestedDate;
        List<StudentCourseScore> relevantScores = allScores.stream()
                .filter(sc -> {
                    LocalDate scoreDate = sc.getBatchClassYearSemester().getClassStart_GC();
                    if (scoreDate == null || finalRequestedDate == null) {
                        return true; // Include if dates are null (shouldn't happen but handle it)
                    }
                    return !scoreDate.isAfter(finalRequestedDate);
                })
                .collect(Collectors.toList());

        // Calculate total grade points and credit hours
        double totalGradePoints = 0.0;
        int totalCreditHours = 0;

        for (StudentCourseScore score : relevantScores) {
            if (score.getScore() == null) {
                continue;
            }

            Course course = score.getCourse();
            int totalCrHrs = course.getTheoryHrs() + course.getLabHrs();

            // Find matching mark interval
            MarkInterval interval = gradingSystem.getIntervals().stream()
                    .filter(i -> score.getScore() >= i.getMin() && score.getScore() <= i.getMax())
                    .findFirst()
                    .orElse(null);

            if (interval != null) {
                double gradePoint = totalCrHrs * interval.getGivenValue();
                totalGradePoints += gradePoint;
                totalCreditHours += totalCrHrs;
            }
        }

        if (totalCreditHours == 0) {
            return 0.0;
        }

        return totalGradePoints / totalCreditHours;
    }

    /**
     * Generates a simplified student copy (without student information) for grade reports.
     * 
     * @param request The request containing studentId, classYearId, and semesterId
     * @return SimplifiedStudentCopyDTO containing only academic context, courses, and GPA info
     * @throws ResourceNotFoundException if student, classyear, semester, or batch-class-year-semester not found
     */
    @Transactional(readOnly = true)
    public SimplifiedStudentCopyDTO generateSimplifiedStudentCopy(StudentCopyRequestDTO request) {
        // Generate full student copy first
        StudentCopyDTO fullCopy = generateStudentCopy(request);
        
        // Convert to simplified version
        SimplifiedStudentCopyDTO simplified = new SimplifiedStudentCopyDTO();
        
        // Academic Context
        SimplifiedStudentCopyDTO.ClassYearInfo classYearInfo = new SimplifiedStudentCopyDTO.ClassYearInfo();
        classYearInfo.setId(fullCopy.getClassyear().getId());
        classYearInfo.setName(fullCopy.getClassyear().getName());
        simplified.setClassyear(classYearInfo);
        
        SimplifiedStudentCopyDTO.SemesterInfo semesterInfo = new SimplifiedStudentCopyDTO.SemesterInfo();
        semesterInfo.setId(fullCopy.getSemester().getId());
        semesterInfo.setName(fullCopy.getSemester().getName());
        simplified.setSemester(semesterInfo);
        
        if (fullCopy.getAcademicYear() != null) {
            SimplifiedStudentCopyDTO.AcademicYearInfo academicYearInfo = new SimplifiedStudentCopyDTO.AcademicYearInfo();
            academicYearInfo.setYearCode(fullCopy.getAcademicYear().getYearCode());
            academicYearInfo.setYearGC(fullCopy.getAcademicYear().getYearGC());
            simplified.setAcademicYear(academicYearInfo);
        }
        
        // Course Grades
        simplified.setCourses(fullCopy.getCourses());
        
        // GPA Information
        simplified.setSemesterGPA(fullCopy.getSemesterGPA());
        simplified.setSemesterCGPA(fullCopy.getSemesterCGPA());
        simplified.setStatus(fullCopy.getStatus());
        
        return simplified;
    }

    /**
     * Generates student copies for multiple students for the same classyear and semester.
     * 
     * @param studentIds List of student IDs
     * @param classYearId ClassYear ID
     * @param semesterId Semester ID
     * @return List of StudentCopyDTO for each student
     */
    @Transactional(readOnly = true)
    public List<StudentCopyDTO> generateStudentCopiesForMultipleStudents(List<Long> studentIds, Long classYearId, String semesterId) {
        List<StudentCopyDTO> studentCopies = new ArrayList<>();
        
        for (Long studentId : studentIds) {
            try {
                StudentCopyRequestDTO request = new StudentCopyRequestDTO();
                request.setStudentId(studentId);
                request.setClassYearId(classYearId);
                request.setSemesterId(semesterId);
                
                StudentCopyDTO studentCopy = generateStudentCopy(request);
                studentCopies.add(studentCopy);
            } catch (Exception e) {
                // Skip students that have errors, continue with others
                continue;
            }
        }
        
        return studentCopies;
    }

    /**
     * Finds the AcademicYear that contains the given date.
     */
    private AcademicYear findAcademicYearForDate(java.time.LocalDate date) {
        if (date == null) {
            return null;
        }

        List<AcademicYear> allAcademicYears = academicYearRepo.findAll();
        return academicYearUtilityService.findAcademicYearByDate(date, allAcademicYears);
    }
}

