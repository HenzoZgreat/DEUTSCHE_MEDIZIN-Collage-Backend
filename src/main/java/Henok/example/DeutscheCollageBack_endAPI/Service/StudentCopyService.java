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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentCopyService {

    @Autowired
    private StudentDetailsRepository studentDetailsRepository;

    @Autowired
    private BatchClassYearSemesterRepo batchClassYearSemesterRepo;
    @Autowired
    private DepartmentBCYSRepository departmentBCYSRepository;

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


    // Constants for grade letter suffixes based on course source
    private static final String SUFFIX_INTERNAL = "";          // sourceId = 1 (from within the school)
    private static final String SUFFIX_REPEAT    = "**";       // sourceId = 2 (repeated course)
    private static final String SUFFIX_EXTERNAL  = "*";        // sourceId = 3 (from outside the school)

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

        // 2. Get requested classyear and semester
        ClassYear classYear = classYearRepository.findById(request.getClassYearId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found with id: " + request.getClassYearId()));

        Semester semester = semesterRepo.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id: " + request.getSemesterId()));

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

        // 4. Get all courses for this student and batchClassYearSemester
        List<StudentCourseScore> courseScores = studentCourseScoreRepo
                .findByStudentAndBatchClassYearSemester(student.getUser(), batchClassYearSemester);


        // 5. Get grading system for student's department
        Department department = student.getDepartmentEnrolled();
        GradingSystem gradingSystem = gradingSystemService.findApplicableGradingSystem(department);


        // 6. Build course grades list
        List<CourseGradeDTO> courseGrades = new ArrayList<>();
        for (StudentCourseScore score : courseScores) {
            if (score.getScore() == null || !score.isReleased()) {
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

            // Determine grade letter suffix based on course source
            String suffix = SUFFIX_INTERNAL;
            if (score.getCourseSource().getSourceID() == 2) {
                suffix = SUFFIX_REPEAT;
            } else if (score.getCourseSource().getSourceID() == 3) {
                suffix = SUFFIX_EXTERNAL;
            }

            courseGrade.setLetterGrade(letterGrade + suffix);
            courseGrade.setGradePoint(gradePoint);

            courseGrades.add(courseGrade);

        }

//        System.out.println("Calculating Semester GPA");
        // 7. Calculate Semester GPA
        double semesterGPA = calculateGPA(courseGrades);
//        System.out.println("Success GPA = " + semesterGPA);

//        System.out.println("Calculatinr Cgpa");
        // 8. Calculate Semester CGPA (cumulative from enrollment until requested semester)
        double semesterCGPA = calculateCGPA(student.getUser(), batchClassYearSemester, gradingSystem);
//        System.out.println("Success, CGPA = " + semesterCGPA);


        // 9. Determine status
        String status = semesterGPA >= MINIMUM_PASSING_GPA ? "PASSED" : "FAILED";

//        System.out.println("Finding Academic year ...");

        // 10. Find AcademicYear
        AcademicYear academicYear = departmentBCYSRepository
                .findByBcysAndDepartment(batchClassYearSemester, department)
                .map(DepartmentBCYS::getAcademicYear)
                .orElse(null);
//        System.out.println("Success, Academic Year = " + (academicYear != null ? academicYear.getAcademicYearGC() : "N/A"));

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
     * Calculates the CGPA for a student.
     *
     * Behavior:
     * - If requestedBCYS is null          → includes ALL released courses for the student
     * - If requestedBCYS is provided      → includes only courses from semesters
     *                                       whose classStart_GC is not after the requested semester's classStart_GC
     *
     * Only considers courses where isReleased = true and score is not null.
     * Uses the provided GradingSystem to map scores to grade points.
     *
     * @param student the student user
     * @param requestedBCYS the target semester (can be null to include everything)
     * @param gradingSystem the grading system to use for grade point mapping
     * @return calculated CGPA (0.0 if no valid courses or total credits = 0)
     */
    public double calculateCGPA(User student, BatchClassYearSemester requestedBCYS, GradingSystem gradingSystem) {
        // Fetch all released course scores, ordered by class start date
        List<StudentCourseScore> allScores = studentCourseScoreRepo
                .findByStudentAndIsReleasedTrue(student);

        if (allScores.isEmpty()) {
            return 0.0;
        }

        List<StudentCourseScore> relevantScores;

        if (requestedBCYS == null) {
            // No semester specified → include ALL released courses
            relevantScores = allScores;
        } else {
            // Semester specified → filter by class start date
            LocalDate requestedDate;
            if (requestedBCYS != null) {
                Department studentDept = studentDetailsRepository.findByUser(student)
                        .map(StudentDetails::getDepartmentEnrolled)
                        .orElse(null);

                if (studentDept != null) {
                    Optional<DepartmentBCYS> deptBCYSOpt = departmentBCYSRepository
                            .findByBcysAndDepartment(requestedBCYS, studentDept);

                    requestedDate = deptBCYSOpt.map(DepartmentBCYS::getClassStartGC).orElse(null);
                } else {
                    requestedDate = null;
                }
            } else {
                requestedDate = null;
            }

            if (requestedDate == null) {
                // Fallback: if requested semester has no date, include everything (rare case)
                relevantScores = allScores;
            } else {
                // Normal case: include only semesters started on or before requestedDate
                relevantScores = allScores.stream()
                        .filter(sc -> {
                            LocalDate scoreSemesterStart = null;
                            Department studentDept = studentDetailsRepository.findByUser(student)
                                    .map(StudentDetails::getDepartmentEnrolled)
                                    .orElse(null);

                            if (studentDept != null) {
                                Optional<DepartmentBCYS> deptBCYSOpt = departmentBCYSRepository
                                        .findByBcysAndDepartment(sc.getBatchClassYearSemester(), studentDept);

                                scoreSemesterStart = deptBCYSOpt.map(DepartmentBCYS::getClassStartGC).orElse(null);
                            }                            // Include if no date OR date is on or before requested date
                            return scoreSemesterStart == null || !scoreSemesterStart.isAfter(requestedDate);
                        })
                        .collect(Collectors.toList());
            }
        }

        // Now calculate grade points and total credits
        double totalGradePoints = 0.0;
        int totalCreditHours = 0;

        for (StudentCourseScore score : relevantScores) {
            if (score.getScore() == null) {
                continue;
            }

            Course course = score.getCourse();
            if (course == null) {
                continue; // safety
            }

            int totalCrHrs = course.getTheoryHrs() + course.getLabHrs();
            if (totalCrHrs <= 0) {
                continue; // avoid division issues or meaningless credits
            }

            // Find the matching grade interval
            MarkInterval interval = gradingSystem.getIntervals().stream()
                    .filter(i -> score.getScore() >= i.getMin() && score.getScore() <= i.getMax())
                    .findFirst()
                    .orElse(null);

            if (interval != null) {
                double gradePoint = totalCrHrs * interval.getGivenValue();
                totalGradePoints += gradePoint;
                totalCreditHours += totalCrHrs;
            }
            // Note: if no interval matches → that course is ignored (common design)
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

