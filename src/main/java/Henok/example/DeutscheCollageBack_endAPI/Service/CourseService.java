package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.CourseDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.ClassYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Course;
import Henok.example.DeutscheCollageBack_endAPI.Entity.CourseCategory;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.ClassYearRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.CourseCategoryRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.CourseRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.SemesterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void addCourses(List<CourseDTO> courseDTOs) {
        if (courseDTOs == null || courseDTOs.isEmpty()) {
            throw new IllegalArgumentException("Course list cannot be null or empty");
        }

        List<Course> courses = courseDTOs.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        for (Course course : courses) {
            validateCourse(course);
            if (courseRepository.existsBycCode(course.getCCode())) {
                throw new IllegalArgumentException("Course code already exists: " + course.getCCode());
            }
            validatePrerequisites(course);
        }

        courseRepository.saveAll(courses);
    }

    public void addCourse(CourseDTO courseDTO) {
        System.out.println("just after the function call checking DTO : " + courseDTO);
        if (courseDTO == null) {
            throw new IllegalArgumentException("Course DTO cannot be null");
        }

        Course course = mapToEntity(courseDTO);
        System.out.println("After Mapping is done, DTO");
        validateCourse(course);

        if (courseRepository.existsBycCode(course.getCCode())) {
            throw new IllegalArgumentException("Course code already exists: " + course.getCCode());
        }

        validatePrerequisites(course);
        courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        if (courses.isEmpty()) {
            throw new ResourceNotFoundException("No courses found");
        }
        return courses;
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    public List<Course> getPrerequisitesByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        Set<Course> prerequisites = course.getPrerequisites();
        if (prerequisites.isEmpty()) {
            throw new ResourceNotFoundException("No prerequisites found for course with id: " + courseId);
        }
        return new ArrayList<>(prerequisites);
    }

    public List<Course> getCoursesByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
        List<Course> courses = courseRepository.findByDepartment(department);
        if (courses.isEmpty()) {
            throw new ResourceNotFoundException("No courses found for department with id: " + departmentId);
        }
        return courses;
    }

    public void updateCourse(Long id, CourseDTO courseDTO) {
        if (courseDTO == null) {
            throw new IllegalArgumentException("Course DTO cannot be null");
        }

        Course existingCourse = getCourseById(id);

        if (courseDTO.getCTitle() != null) {
            existingCourse.setCTitle(courseDTO.getCTitle());
        }

        String newCCode = courseDTO.getCCode();
        if (newCCode != null && !existingCourse.getCCode().equals(newCCode) && courseRepository.existsBycCode(newCCode)) {
            throw new IllegalArgumentException("Course code already exists: " + newCCode);
        }
        if (newCCode != null && !newCCode.isEmpty()) {
            existingCourse.setCCode(newCCode);
        }

        if (courseDTO.getTheoryHrs() != null) {
            existingCourse.setTheoryHrs(courseDTO.getTheoryHrs());
        }

        if (courseDTO.getLabHrs() != null) {
            existingCourse.setLabHrs(courseDTO.getLabHrs());
        }

        if (courseDTO.getCCatagoryID() != null) {
            CourseCategory category = courseCategoryRepository.findById(courseDTO.getCCatagoryID())
                    .orElseThrow(() -> new ResourceNotFoundException("Course category not found with id: " + courseDTO.getCCatagoryID()));
            existingCourse.setCategory(category);
        }

        if (courseDTO.getDepartmentID() != null) {
            Department department = departmentRepository.findById(courseDTO.getDepartmentID())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + courseDTO.getDepartmentID()));
            existingCourse.setDepartment(department);
        }

        if (courseDTO.getClassYearID() != null) {
            ClassYear classYear = classYearRepository.findById(courseDTO.getClassYearID())
                    .orElseThrow(() -> new ResourceNotFoundException("Class year not found with id: " + courseDTO.getClassYearID()));
            existingCourse.setClassYear(classYear);
        }

        if (courseDTO.getSemesterID() != null && !courseDTO.getSemesterID().isEmpty()) {
            Semester semester = semesterRepository.findById(courseDTO.getSemesterID())
                    .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id: " + courseDTO.getSemesterID()));
            existingCourse.setSemester(semester);
        }

        if (courseDTO.getPrerequisiteIds() != null) {
            Set<Course> prerequisites = courseDTO.getPrerequisiteIds().stream()
                    .map(prereqId -> courseRepository.findById(prereqId)
                            .orElseThrow(() -> new ResourceNotFoundException("Prerequisite course not found with id: " + prereqId)))
                    .collect(Collectors.toSet());
            validatePrerequisites(id, prerequisites);
            existingCourse.setPrerequisites(prerequisites);
        }

        courseRepository.save(existingCourse);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    public void addPrerequisite(Long courseId, Long prerequisiteId) {
        Course course = getCourseById(courseId);
        Course prerequisite = getCourseById(prerequisiteId);

        if (courseId.equals(prerequisiteId)) {
            throw new IllegalArgumentException("A course cannot be its own prerequisite");
        }

        if (hasCircularDependency(course, prerequisite)) {
            throw new IllegalArgumentException("Adding this prerequisite would create a circular dependency");
        }

        course.getPrerequisites().add(prerequisite);
        courseRepository.save(course);
    }

    public void removePrerequisite(Long courseId, Long prerequisiteId) {
        Course course = getCourseById(courseId);
        Course prerequisite = getCourseById(prerequisiteId);

        if (!course.getPrerequisites().removeIf(prereq -> prereq.getCID().equals(prerequisiteId))) {
            throw new ResourceNotFoundException("Prerequisite with id " + prerequisiteId + " not found for course " + courseId);
        }

        courseRepository.save(course);
    }

    // ==================== Helper Functions ==========================================================================================
    private Course mapToEntity(CourseDTO dto) {
        CourseCategory category = courseCategoryRepository.findById(dto.getCCatagoryID())
                .orElseThrow(() -> new ResourceNotFoundException("Course category not found with id: " + dto.getCCatagoryID()));
        Department department = departmentRepository.findById(dto.getDepartmentID())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentID()));
        ClassYear classYear = classYearRepository.findById(dto.getClassYearID())
                .orElseThrow(() -> new ResourceNotFoundException("Class year not found with id: " + dto.getClassYearID()));
        Semester semester = semesterRepository.findById(dto.getSemesterID())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id: " + dto.getSemesterID()));

        Set<Course> prerequisites = dto.getPrerequisiteIds() != null
                ? dto.getPrerequisiteIds().stream()
                .map(id -> courseRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Prerequisite course not found with id: " + id)))
                .collect(Collectors.toSet())
                : new HashSet<>();

        return new Course(null, dto.getCTitle(), dto.getCCode(),
                dto.getTheoryHrs() != null ? dto.getTheoryHrs() : 0,
                dto.getLabHrs() != null ? dto.getLabHrs() : 0,
                category, department, prerequisites, classYear, semester);
    }

    private void validateCourse(Course course) {
        if (course.getCTitle() == null || course.getCTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be null or empty");
        }
        if (course.getCCode() == null || course.getCCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be null or empty");
        }
        if (course.getTheoryHrs() == null || course.getTheoryHrs() < 0) {
            throw new IllegalArgumentException("Theory hours must be a non-negative integer");
        }
        if (course.getLabHrs() == null || course.getLabHrs() < 0) {
            throw new IllegalArgumentException("Lab hours must be a non-negative integer");
        }
        if (course.getCategory() == null) {
            throw new IllegalArgumentException("Course category cannot be null");
        }
        if (course.getDepartment() == null) {
            throw new IllegalArgumentException("Department cannot be null");
        }
        if (course.getClassYear() == null) {
            throw new IllegalArgumentException("Class year cannot be null");
        }
        if (course.getSemester() == null) {
            throw new IllegalArgumentException("Semester cannot be null");
        }
    }

    private void validatePrerequisites(Course course) {
        if (course.getCID() != null && course.getPrerequisites().contains(course)) {
            throw new IllegalArgumentException("A course cannot be its own prerequisite");
        }
        if (hasCircularDependency(course, course.getPrerequisites())) {
            throw new IllegalArgumentException("Prerequisites would create a circular dependency");
        }
    }

    private void validatePrerequisites(Long courseId, Set<Course> prerequisites) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        if (prerequisites.contains(course)) {
            throw new IllegalArgumentException("A course cannot be its own prerequisite");
        }
        if (hasCircularDependency(course, prerequisites)) {
            throw new IllegalArgumentException("Prerequisites would create a circular dependency");
        }
    }

    private boolean hasCircularDependency(Course course, Course prerequisite) {
        if (course.getCID() == null || prerequisite.getCID() == null) {
            return false;
        }
        Set<Long> visited = new HashSet<>();
        return checkCircularDependency(prerequisite, course.getCID(), visited);
    }

    private boolean hasCircularDependency(Course course, Set<Course> prerequisites) {
        if (course.getCID() == null) {
            return false;
        }
        Set<Long> visited = new HashSet<>();
        for (Course prereq : prerequisites) {
            if (checkCircularDependency(prereq, course.getCID(), visited)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCircularDependency(Course current, Long targetId, Set<Long> visited) {
        if (current.getCID() == null || visited.contains(current.getCID())) {
            return false;
        }
        if (current.getCID().equals(targetId)) {
            return true;
        }
        visited.add(current.getCID());
        for (Course prereq : current.getPrerequisites()) {
            if (checkCircularDependency(prereq, targetId, visited)) {
                return true;
            }
        }
        return false;
    }
}