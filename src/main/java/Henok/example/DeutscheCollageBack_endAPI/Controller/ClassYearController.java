package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.ClassYearDTO;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.ClassYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-years")
public class ClassYearController {

    @Autowired
    private ClassYearService classYearService;

    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<ClassYearDTO> createClassYear(@RequestBody ClassYearDTO classYearDTO) {
        try {
            ClassYearDTO created = classYearService.createClassYear(classYearDTO);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<List<ClassYearDTO>> getAllClassYears() {
        return ResponseEntity.ok(classYearService.getAllClassYears());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<ClassYearDTO> getClassYearById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(classYearService.getClassYearById(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<ClassYearDTO> updateClassYear(@PathVariable Long id, @RequestBody ClassYearDTO classYearDTO) {
        try {
            return ResponseEntity.ok(classYearService.updateClassYear(id, classYearDTO));
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<Void> deleteClassYear(@PathVariable Long id) {
        try {
            classYearService.deleteClassYear(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
