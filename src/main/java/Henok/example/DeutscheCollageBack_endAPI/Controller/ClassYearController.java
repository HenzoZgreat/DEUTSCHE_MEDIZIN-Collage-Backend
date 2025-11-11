package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.ClassYearDTO;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.ClassYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-years")
public class ClassYearController {

    @Autowired
    private ClassYearService classYearService;

    //Creates a new Classyear
    @PostMapping
    public ResponseEntity<?> createClassYear(@RequestBody ClassYearDTO classYearDTO) {
        try {
            ClassYearDTO created = classYearService.createClassYear(classYearDTO);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }
    }

    //Retrieves all Classyears
    @GetMapping
    public ResponseEntity<?> getAllClassYears() {
        try {
            return ResponseEntity.ok(classYearService.getAllClassYears());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    //Get a Class year by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getClassYearById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(classYearService.getClassYearById(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    //Update a class year by using ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClassYear(@PathVariable Long id, @RequestBody ClassYearDTO classYearDTO) {
        try {
            return ResponseEntity.ok(classYearService.updateClassYear(id, classYearDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }
    }

    //Delete a class year by using ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClassYear(@PathVariable Long id) {
        try {
            classYearService.deleteClassYear(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }
    }

    //Helper Function
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred: " + ex.getMessage()));
    }
}
