package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.Entity.CourseSource;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.CourseSourceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseSourceService {

    private final CourseSourceRepo courseSourceRepository;

    // Creates and saves a new course source
    public CourseSource createCourseSource(CourseSource courseSource) {
        if (courseSource.getSourceName() == null || courseSource.getSourceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Source name cannot be empty");
        }
        courseSource.setSourceName(courseSource.getSourceName().trim());
        return courseSourceRepository.save(courseSource);
    }

    // Returns all course sources
    public List<CourseSource> getAllCourseSources() {
        return courseSourceRepository.findAll();
    }

    // Retrieves a single course source by ID
    public CourseSource getCourseSourceById(Long id) {
        return courseSourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course source not found with id: " + id));
    }

    // Updates only the fields provided in the map (partial update)
    // Why: Safe PATCH operation — only changes sent fields
    public CourseSource updateCourseSource(Long id, Map<String, Object> updates) {
        CourseSource existing = getCourseSourceById(id);

        updates.forEach((key, value) -> {
            if ("sourceName".equalsIgnoreCase(key)) {
                if (value == null || String.valueOf(value).trim().isEmpty()) {
                    throw new IllegalArgumentException("Source name cannot be empty");
                }
                existing.setSourceName(String.valueOf(value).trim());
            }
            // Add more fields here in the future if entity grows
        });

        return courseSourceRepository.save(existing);
    }

    // Deletes a course source by ID
    public void deleteCourseSource(Long id) {
        if (!courseSourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course source not found with id: " + id);
        }
        courseSourceRepository.deleteById(id);
    }
}
