package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips.SlipCourseDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips.SlipPreviewRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips.StudentSlipPreviewDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramModality;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.BatchClassYearSemesterRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.CourseRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentBCYSRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.StudentDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlipService {

    private final StudentDetailsRepository studentDetailsRepo;
    private final CourseRepo courseRepo;
    private final BatchClassYearSemesterRepo bcysRepo;
    private final DepartmentBCYSRepository departmentBCYSRepository;

    // Main method: Generate preview for multiple students
    public List<StudentSlipPreviewDTO> generateSlipPreviews(SlipPreviewRequest request) {

        // Validate input
        if (request.getStudentIds() == null || request.getStudentIds().isEmpty()) {
            throw new IllegalArgumentException("studentIds cannot be empty");
        }
        if (request.getCourseIds() == null || request.getCourseIds().isEmpty()) {
            throw new IllegalArgumentException("courseIds cannot be empty");
        }

        // 1. Fetch all students with necessary relations in ONE query
        List<StudentDetails> students = studentDetailsRepo.findAllByIdInWithRelations(request.getStudentIds());
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("No students found with provided IDs");
        }
        if (students.size() != request.getStudentIds().size()) {
            throw new ResourceNotFoundException("Some student IDs were not found");
        }

        // 2. Fetch all selected courses
        List<Course> courses = courseRepo.findAllById(request.getCourseIds());
        if (courses.isEmpty()) {
            throw new ResourceNotFoundException("No courses found with provided IDs");
        }
        if (courses.size() != request.getCourseIds().size()) {
            throw new IllegalArgumentException("Some course IDs are invalid");
        }

        // 3. Map courses to DTO once
        List<SlipCourseDTO> courseDTOs = courses.stream()
                .map(this::mapToSlipCourseDTO)
                .toList();

        // 4. Determine BCYS context (from request or student's current)
        BatchClassYearSemester bcys = resolveBatchContext(request, students.get(0));

        // 5. Build preview for each student
        return students.stream()
                .map(student -> buildStudentSlipPreview(student, courseDTOs, bcys))
                .toList();
    }

    public List<SlipCourseDTO> getAvailableCoursesForStudents(List<Long> studentIds) {

        if (studentIds.isEmpty()) {
            throw new IllegalArgumentException("Student IDs cannot be empty");
        }

        // 1. Get distinct departments of these students in ONE query
        List<Department> departments = studentDetailsRepo.findDistinctDepartmentsByStudentIds(studentIds);

//        System.out.println("the Distinct Departments are : ");
//        for (Department department : departments) {
//            System.out.println("\t" + department.getDeptName());
//        }

        if (departments.isEmpty()) {
            throw new ResourceNotFoundException("No students found with provided IDs");
        }

        // 2. Get all courses from these departments → DISTINCT
        List<Course> courses = courseRepo.findDistinctByDepartmentIn(departments);

        // 3. Map to DTO and sort by code
        return courses.stream()
                .map(this::mapToSlipCourseDTO)
                .sorted(Comparator.comparing(SlipCourseDTO::getCode))
                .toList();
    }

    private BatchClassYearSemester resolveBatchContext(SlipPreviewRequest request, StudentDetails sampleStudent) {
        if (request.getBatchClassYearSemesterId() != null) {
            return bcysRepo.findById(request.getBatchClassYearSemesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found"));
        }
        return sampleStudent.getBatchClassYearSemester();
    }

    private StudentSlipPreviewDTO buildStudentSlipPreview(StudentDetails student,
                                                          List<SlipCourseDTO> courses,
                                                          BatchClassYearSemester bcys) {

        StudentSlipPreviewDTO dto = new StudentSlipPreviewDTO();
        dto.setStudentId(student.getId());
        dto.setUsername(student.getUser().getUsername());

        // Full Name (English)
        dto.setFullNameEng(String.format("%s %s %s",
                student.getFirstNameENG(),
                student.getFatherNameENG(),
                student.getGrandfatherNameENG()).trim());

        dto.setFullNameAmh(String.format("%s %s %s",
                student.getFirstNameAMH(),
                student.getFatherNameAMH(),
                student.getGrandfatherNameAMH()).trim());

        dto.setAge(student.getAge());
        dto.setGender(student.getGender());

        // Department
        Department dept = student.getDepartmentEnrolled();
        dto.setDepartmentId(dept.getDepartmentCode());
        dto.setDepartmentName(dept.getDeptName());
        dto.setDepartmentCode(dept.getDepartmentCode());

        // Class Year & Semester from BCYS
        dto.setClassYearId(bcys.getClassYear().getId());
        dto.setClassYearName(bcys.getClassYear().getClassYear());
        dto.setSemesterId(bcys.getSemester().getAcademicPeriodCode());
        dto.setSemesterName(bcys.getSemester().getAcademicPeriod());

        // Academic Year from DepartmentBCYS link
        AcademicYear ay = departmentBCYSRepository
                .findByBcysAndDepartment(bcys, dept)
                .map(DepartmentBCYS::getAcademicYear)
                .orElse(null);        if (ay != null) {
            dto.setAcademicYearCode(ay.getYearCode());
            dto.setAcademicYearGC(ay.getAcademicYearGC());
            dto.setAcademicYearEC(ay.getAcademicYearEC());
        }

        // Enrollment Type = Program Modality
        ProgramModality modality = student.getProgramModality();
        dto.setEnrollmentTypeCode(modality.getModalityCode());
        dto.setEnrollmentTypeName(modality.getModality());

        dto.setCourses(courses);
        dto.setBatchDisplayName(bcys.getDisplayName());

        return dto;
    }

    private SlipCourseDTO mapToSlipCourseDTO(Course c) {
        SlipCourseDTO dto = new SlipCourseDTO();
        dto.setCourseId(c.getCID());
        dto.setCode(c.getCCode());
        dto.setTitle(c.getCTitle());
        dto.setLectureHours(c.getTheoryHrs());
        dto.setLabHours(c.getLabHrs());
        dto.setTotalHours(c.getTheoryHrs() + c.getLabHrs());
        return dto;
    }
}