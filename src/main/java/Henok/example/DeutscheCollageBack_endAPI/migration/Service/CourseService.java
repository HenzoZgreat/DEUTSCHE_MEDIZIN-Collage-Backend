package Henok.example.DeutscheCollageBack_endAPI.migration.Service;

import Henok.example.DeutscheCollageBack_endAPI.Entity.ClassYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Course;
import Henok.example.DeutscheCollageBack_endAPI.Entity.CourseCategory;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.ClassYearRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.CourseCategoryRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.CourseRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.SemesterRepo;
import Henok.example.DeutscheCollageBack_endAPI.migration.DTO.CourseCreateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

// CourseService.java
// Service handling bulk course import with proper validation and prerequisite resolution.
@Service
public class CourseService {

    @Autowired
    private CourseRepo courseRepository;

    @Autowired
    private CourseCategoryRepo courseCategoryRepository;

    @Autowired
    private DepartmentRepo departmentRepository;

    @Autowired
    private ClassYearRepository classYearRepository;

    @Autowired
    private SemesterRepo semesterRepository;

    /**
     * Bulk imports a list of courses.
     * Steps:
     * 1. Validate no duplicate codes in input and no conflicts with existing DB entries
     * 2. Create and save all courses without prerequisites first (to get IDs)
     * 3. Resolve and set prerequisites (from batch or existing DB)
     * 4. Save again with prerequisites
     *
     * @param dtos List of CourseCreateDTO
     * @return List of created Course entities
     */
    /**
     * Bulk imports courses.
     * If a single course fails validation (e.g., missing category, invalid reference, etc.),
     * it is skipped, recorded as failed, and processing continues with the next course.
     *
     * @param dtos List of CourseCreateDTO
     * @return BulkImportResponseDTO containing imported courses and list of failed ones
     */
    @Transactional
    public BulkImportResponseDTO bulkImport(List<CourseCreateDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new BadRequestException("Import data cannot be empty");
        }

        BulkImportResponseDTO response = new BulkImportResponseDTO();
        List<Course> successfulCourses = new ArrayList<>();
        List<String> failedCourses = new ArrayList<>();

        // Map for quick lookup of courses created in this batch (by code)
        Map<String, Course> codeToCourse = new HashMap<>();

        // First pass: try to create each course individually
        for (CourseCreateDTO dto : dtos) {
            try {
                // Basic required field validation
                if (dto.getTitle() == null || dto.getTitle().trim().isEmpty() ||
                        dto.getCode() == null || dto.getCode().trim().isEmpty() ||
                        dto.getCategoryId() == null) {
                    throw new BadRequestException("Title, code and categoryId are required");
                }

                String code = dto.getCode().trim();

                // Skip if this code already exists in DB
                if (courseRepository.existsByCCode(code)) {
                    failedCourses.add(dto.getTitle().trim() + "(" + code + ")");
                    continue;
                }

                // Skip if code duplicates within the current batch
                if (codeToCourse.containsKey(code)) {
                    failedCourses.add(dto.getTitle().trim() + "(" + code + ")");
                    continue;
                }

                Course course = new Course();
                course.setCTitle(dto.getTitle().trim());
                course.setCCode(code);
                course.setTheoryHrs(dto.getTheoryHrs() != null ? dto.getTheoryHrs() : 0);
                course.setLabHrs(dto.getLabHrs() != null ? dto.getLabHrs() : 0);

                // Required: Category
                CourseCategory category = courseCategoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.getCategoryId()));
                course.setCategory(category);

                // Optional: Department
                if (dto.getDepartmentId() != null) {
                    Department dept = departmentRepository.findById(dto.getDepartmentId())
                            .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + dto.getDepartmentId()));
                    course.setDepartment(dept);
                }

                // Optional: ClassYear
                if (dto.getClassYearId() != null) {
                    ClassYear cy = classYearRepository.findById(dto.getClassYearId())
                            .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found: " + dto.getClassYearId()));
                    course.setClassYear(cy);
                }

                // Optional: Semester
                if (dto.getSemesterId() != null) {
                    Semester sem = semesterRepository.findById(dto.getSemesterId())
                            .orElseThrow(() -> new ResourceNotFoundException("Semester not found: " + dto.getSemesterId()));
                    course.setSemester(sem);
                }

                course.setPrerequisites(new HashSet<>());

                successfulCourses.add(course);
                codeToCourse.put(code, course);

            } catch (Exception e) {
                // Any validation or lookup failure → skip this course
                failedCourses.add(dto.getTitle().trim() + "(" + dto.getCode().trim() + ")");
            }
        }

        // Save all valid courses (they now have IDs)
        if (!successfulCourses.isEmpty()) {
            courseRepository.saveAll(successfulCourses);

            // Second pass: set prerequisites only for successfully imported courses
            for (Course course : successfulCourses) {
                CourseCreateDTO originalDto = dtos.stream()
                        .filter(d -> d.getCode().equals(course.getCCode()))
                        .findFirst()
                        .orElse(null);

                if (originalDto != null && originalDto.getPrerequisiteCodes() != null) {
                    Set<Course> prereqs = new HashSet<>();
                    for (String prereqCode : originalDto.getPrerequisiteCodes()) {
                        Course prereq = codeToCourse.get(prereqCode);

                        if (prereq == null) {
                            prereq = courseRepository.findByCCode(prereqCode).orElse(null);
                        }

                        // If prerequisite still not found → silently skip it (course already imported)
                        if (prereq != null) {
                            prereqs.add(prereq);
                        }
                    }
                    course.setPrerequisites(prereqs);
                }
            }

            // Final save with prerequisites
            courseRepository.saveAll(successfulCourses);
        }

        response.setImportedCourses(successfulCourses);
        response.setFailedCourses(failedCourses);

        return response;
    }
}
