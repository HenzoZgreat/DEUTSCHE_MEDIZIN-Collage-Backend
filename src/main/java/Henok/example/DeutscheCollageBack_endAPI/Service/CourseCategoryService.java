package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.CourseCategoryDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.CourseCategory;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.CourseCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseCategoryService {

    @Autowired
    private CourseCategoryRepo courseCategoryRepository;

    public void addCourseCategories(List<CourseCategoryDTO> courseCategoryDTOs) {
        if (courseCategoryDTOs == null || courseCategoryDTOs.isEmpty()) {
            throw new IllegalArgumentException("Course category list cannot be null or empty");
        }

        List<CourseCategory> courseCategories = courseCategoryDTOs.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        for (CourseCategory category : courseCategories) {
            validateCourseCategory(category);
            if (courseCategoryRepository.existsByCatName(category.getCatName())) {
                throw new IllegalArgumentException("Course category name already exists: " + category.getCatName());
            }
        }

        courseCategoryRepository.saveAll(courseCategories);
    }

    public void addCourseCategory(CourseCategoryDTO courseCategoryDTO) {
        if (courseCategoryDTO == null) {
            throw new IllegalArgumentException("Course category DTO cannot be null");
        }

        CourseCategory courseCategory = mapToEntity(courseCategoryDTO);
        validateCourseCategory(courseCategory);

        if (courseCategoryRepository.existsByCatName(courseCategory.getCatName())) {
            throw new IllegalArgumentException("Course category name already exists: " + courseCategory.getCatName());
        }

        courseCategoryRepository.save(courseCategory);
    }

    public List<CourseCategory> getAllCourseCategories() {
        List<CourseCategory> courseCategories = courseCategoryRepository.findAll();
        if (courseCategories.isEmpty()) {
            throw new ResourceNotFoundException("No course categories found");
        }
        return courseCategories;
    }

    public CourseCategory getCourseCategoryById(Long id) {
        return courseCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course category not found with id: " + id));
    }

    public void updateCourseCategory(Long id, CourseCategoryDTO courseCategoryDTO) {
        if (courseCategoryDTO == null) {
            throw new IllegalArgumentException("Course category DTO cannot be null");
        }

        CourseCategory existingCategory = getCourseCategoryById(id);
        String newCatName = courseCategoryDTO.getCatName();

        if (!existingCategory.getCatName().equals(newCatName) &&
                courseCategoryRepository.existsByCatName(newCatName)) {
            throw new IllegalArgumentException("Course category name already exists: " + newCatName);
        }

        existingCategory.setCatName(newCatName);
        validateCourseCategory(existingCategory);

        courseCategoryRepository.save(existingCategory);
    }

    public void deleteCourseCategory(Long id) {
        if (!courseCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course category not found with id: " + id);
        }
        courseCategoryRepository.deleteById(id);
    }

    private CourseCategory mapToEntity(CourseCategoryDTO dto) {
        return new CourseCategory(null, dto.getCatName());
    }

    private void validateCourseCategory(CourseCategory courseCategory) {
        if (courseCategory.getCatName() == null || courseCategory.getCatName().trim().isEmpty()) {
            throw new IllegalArgumentException("Course category name cannot be null or empty");
        }
    }
}