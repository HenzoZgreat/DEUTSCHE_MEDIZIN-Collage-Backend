package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.CourseDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Course;
import Henok.example.DeutscheCollageBack_endAPI.Entity.CourseCategory;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.CourseCategoryRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.CourseRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentRepo;
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

    public void updateCourse(Long id, CourseDTO courseDTO) {
        if (courseDTO == null) {
            throw new IllegalArgumentException("Course DTO cannot be null");
        }

        Course existingCourse = getCourseById(id);
        String newCCode = courseDTO.getCCode();

        if (!existingCourse.getCCode().equals(newCCode) && courseRepository.existsBycCode(newCCode)) {
            throw new IllegalArgumentException("Course code already exists: " + newCCode);
        }

        existingCourse.setCTitle(courseDTO.getCTitle());
        existingCourse.setCCode(newCCode);
        existingCourse.setTheoryHrs(courseDTO.getTheoryHrs());
        existingCourse.setLabHrs(courseDTO.getLabHrs());

        CourseCategory category = courseCategoryRepository.findById(courseDTO.getCCatagoryID())
                .orElseThrow(() -> new ResourceNotFoundException("Course category not found with id: " + courseDTO.getCCatagoryID()));
        existingCourse.setCategory(category);

        Department department = departmentRepository.findById(courseDTO.getDepartmentID())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + courseDTO.getDepartmentID()));
        existingCourse.setDepartment(department);

        Set<Course> prerequisites = courseDTO.getPrerequisiteIds() != null
                ? courseDTO.getPrerequisiteIds().stream()
                .map(prereqId -> courseRepository.findById(prereqId)
                        .orElseThrow(() -> new ResourceNotFoundException("Prerequisite course not found with id: " + prereqId)))
                .collect(Collectors.toSet())
                : new HashSet<>();
        validatePrerequisites(id, prerequisites);
        existingCourse.setPrerequisites(prerequisites);

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

    private Course mapToEntity(CourseDTO dto) {
        CourseCategory category = courseCategoryRepository.findById(dto.getCCatagoryID())
                .orElseThrow(() -> new ResourceNotFoundException("Course category not found with id: " + dto.getCCatagoryID()));
        Department department = departmentRepository.findById(dto.getDepartmentID())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentID()));

        Set<Course> prerequisites = dto.getPrerequisiteIds() != null
                ? dto.getPrerequisiteIds().stream()
                .map(id -> courseRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Prerequisite course not found with id: " + id)))
                .collect(Collectors.toSet())
                : new HashSet<>();

        return new Course(null, dto.getCTitle(), dto.getCCode(), dto.getTheoryHrs() != null ? dto.getTheoryHrs() : 0,
                dto.getLabHrs() != null ? dto.getLabHrs() : 0, category, department, prerequisites);
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