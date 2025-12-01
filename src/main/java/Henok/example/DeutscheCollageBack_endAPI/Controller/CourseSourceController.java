package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.Entity.CourseSource;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.CourseSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-sources")
@RequiredArgsConstructor
public class CourseSourceController {

    private final CourseSourceService courseSourceService;

    // CREATE - Add new course source
    @PostMapping
    public ResponseEntity<?> createCourseSource(@RequestBody CourseSource courseSource) {
        try {
            CourseSource saved = courseSourceService.createCourseSource(courseSource);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Course source with this name already exists"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create course source"));
        }
    }

    // READ - Get all course sources
    @GetMapping
    public ResponseEntity<List<CourseSource>> getAllCourseSources() {
        return ResponseEntity.ok(courseSourceService.getAllCourseSources());
    }

    // READ - Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseSourceById(@PathVariable Long id) {
        try {
            CourseSource source = courseSourceService.getCourseSourceById(id);
            return ResponseEntity.ok(source);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // PATCH - Partial update (only non-null/non-empty fields)
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCourseSource(@PathVariable Long id,
                                                @RequestBody Map<String, Object> updates) {
        try {
            CourseSource updated = courseSourceService.updateCourseSource(id, updates);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update course source"));
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourseSource(@PathVariable Long id) {
        try {
            courseSourceService.deleteCourseSource(id);
            return ResponseEntity.ok(Map.of("message", "Course source deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Cannot delete: This course source is being used by one or more courses"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete course source"));
        }
    }
}