package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.GradingSystemDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.UpdateActiveStatusDTO;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.GradingSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grading-systems")
public class GradingSystemController {

    @Autowired
    private GradingSystemService gradingSystemService;

    /**
     * Creates a new grading system.
     * @param dto The grading system data.
     * @return The created grading system DTO or error response.
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody GradingSystemDTO dto) {
        try {
            GradingSystemDTO created = gradingSystemService.createGradingSystem(dto);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Retrieves a grading system by ID.
     * @param id The grading system ID.
     * @return The grading system DTO or error response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            GradingSystemDTO dto = gradingSystemService.getGradingSystem(id);
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Retrieves all grading systems.
     * @return List of grading system DTOs.
     */
    @GetMapping
    public ResponseEntity<List<GradingSystemDTO>> getAll() {
        return ResponseEntity.ok(gradingSystemService.getAllGradingSystems());
    }

    /**
     * Updates a grading system.
     * @param id The grading system ID.
     * @param dto The updated data.
     * @return The updated grading system DTO or error response.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody GradingSystemDTO dto) {
        try {
            GradingSystemDTO updated = gradingSystemService.updateGradingSystem(id, dto);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Deletes a grading system.
     * @param id The grading system ID.
     * @return No content on success or error response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            gradingSystemService.deleteGradingSystem(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Updates the active status of a grading system.
     * Ensures only one grading system is active per department.
     * @param id The grading system ID.
     * @param request The request containing the new active status.
     * @return The updated grading system DTO or error response.
     */
    @PutMapping("/{id}/active-status")
    public ResponseEntity<?> updateActiveStatus(@PathVariable Long id, @RequestBody UpdateActiveStatusDTO request) {
        try {
            GradingSystemDTO updated = gradingSystemService.updateActiveStatus(id, request.isActive());
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update active status: " + e.getMessage()));
        }
    }

    // Explanation: RESTful controller for GradingSystem CRUD.
    // Why: Supports departmentId in DTO for department-specific systems; handles errors appropriately.
}