package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.Entity.CourseCategory;
import Henok.example.DeutscheCollageBack_endAPI.Repository.CourseCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseCategoryService {

    @Autowired
    private CourseCategoryRepo courseCategoryRepository;

    public void addCourseCategories(List<CourseCategory> courseCategories) {
        courseCategoryRepository.saveAll(courseCategories);
    }

    public List<CourseCategory> getAllCourseCategories() {
        return courseCategoryRepository.findAll();
    }

    public void addCourseCategory(CourseCategory courseCategory) {
        courseCategoryRepository.save(courseCategory);
    }

    public CourseCategory getCourseCategoryById(Long id) {
        return courseCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course category not found: " + id));
    }

    public void updateCourseCategory(Long id, CourseCategory courseCategory) {
        CourseCategory existingCategory = getCourseCategoryById(id);
        existingCategory.setCatName(courseCategory.getCatName());
        courseCategoryRepository.save(existingCategory);
    }

    public void deleteCourseCategory(Long id) {
        courseCategoryRepository.deleteById(id);
    }
}