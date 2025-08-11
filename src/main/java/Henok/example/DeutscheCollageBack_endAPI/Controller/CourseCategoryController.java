package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.Entity.CourseCategory;
import Henok.example.DeutscheCollageBack_endAPI.Service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-categories")
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @PostMapping
    public ResponseEntity<String> addCourseCategories(@RequestBody List<CourseCategory> courseCategories) {
        courseCategoryService.addCourseCategories(courseCategories);
        return ResponseEntity.ok("Course categories added successfully");
    }

    @GetMapping
    public ResponseEntity<List<CourseCategory>> getAllCourseCategories() {
        return ResponseEntity.ok(courseCategoryService.getAllCourseCategories());
    }

    @PostMapping("/single")
    public ResponseEntity<String> addCourseCategory(@RequestBody CourseCategory courseCategory) {
        courseCategoryService.addCourseCategory(courseCategory);
        return ResponseEntity.ok("Course category added successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseCategory> getCourseCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(courseCategoryService.getCourseCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCourseCategory(@PathVariable Long id, @RequestBody CourseCategory courseCategory) {
        courseCategoryService.updateCourseCategory(id, courseCategory);
        return ResponseEntity.ok("Course category updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourseCategory(@PathVariable Long id) {
        courseCategoryService.deleteCourseCategory(id);
        return ResponseEntity.ok("Course category deleted successfully");
    }
}
