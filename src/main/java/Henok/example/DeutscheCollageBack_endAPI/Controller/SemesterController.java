package Henok.example.DeutscheCollageBack_endAPI.Controller;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.SemesterDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;

    // Receives a list of Semesters and add them all
    @PostMapping
    public ResponseEntity<?> addSemesters(@RequestBody List<SemesterDTO> semesterDTOs) {
        try {
            semesterService.addSemesters(semesterDTOs);
            return ResponseEntity.ok("Semesters added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add semesters: " + e.getMessage()));
        }
    }

    // Adds a single Semester Record
    @PostMapping("/single")
    public ResponseEntity<?> addSemester(@RequestBody SemesterDTO semesterDTO) {
        try {
            semesterService.addSemester(semesterDTO);
            return ResponseEntity.ok("Semester added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add semester: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSemesters() {
        try {
            List<Semester> semesters = semesterService.getAllSemesters();
            return ResponseEntity.ok(semesters);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve semesters: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSemesterById(@PathVariable String id) {
        try {
            Semester semester = semesterService.getSemesterById(id);
            return ResponseEntity.ok(semester);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve semester: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSemester(@PathVariable String id, @RequestBody SemesterDTO semesterDTO) {
        try {
            semesterService.updateSemester(id, semesterDTO);
            return ResponseEntity.ok("Semester updated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update semester: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSemester(@PathVariable String id) {
        try {
            semesterService.deleteSemester(id);
            return ResponseEntity.ok("Semester deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete semester: " + e.getMessage()));
        }
    }
}
