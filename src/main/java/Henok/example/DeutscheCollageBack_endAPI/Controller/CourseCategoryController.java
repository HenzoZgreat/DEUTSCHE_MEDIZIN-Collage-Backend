package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.CourseCategoryDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.CourseCategory;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/course-categories")
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @PostMapping
    public ResponseEntity<?> addCourseCategories(@RequestBody List<CourseCategoryDTO> courseCategoryDTOs) {
        try {
            courseCategoryService.addCourseCategories(courseCategoryDTOs);
            return ResponseEntity.ok("Course categories added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add course categories: " + e.getMessage()));
        }
    }

    @PostMapping("/single")
    public ResponseEntity<?> addCourseCategory(@RequestBody CourseCategoryDTO courseCategoryDTO) {
        try {
            courseCategoryService.addCourseCategory(courseCategoryDTO);
            return ResponseEntity.ok("Course category added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add course category: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCourseCategories() {
        try {
            List<CourseCategory> courseCategories = courseCategoryService.getAllCourseCategories();
            return ResponseEntity.ok(courseCategories);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve course categories: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseCategoryById(@PathVariable Long id) {
        try {
            CourseCategory courseCategory = courseCategoryService.getCourseCategoryById(id);
            return ResponseEntity.ok(courseCategory);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve course category: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourseCategory(@PathVariable Long id, @RequestBody CourseCategoryDTO courseCategoryDTO) {
        try {
            courseCategoryService.updateCourseCategory(id, courseCategoryDTO);
            return ResponseEntity.ok("Course category updated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update course category: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourseCategory(@PathVariable Long id) {
        try {
            courseCategoryService.deleteCourseCategory(id);
            return ResponseEntity.ok("Course category deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete course category: " + e.getMessage()));
        }
    }
}