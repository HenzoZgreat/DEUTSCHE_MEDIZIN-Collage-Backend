package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.BatchClassYearSemesterDTO;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.BatchClassYearSemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bcsy")
public class BatchClassYearSemesterController {

    @Autowired
    private BatchClassYearSemesterService batchClassYearSemesterService;

    /**
     * Creates a new batch-class-year-semester combination.
     * @param dto The batch-class-year-semester data.
     * @return The created BatchClassYearSemesterDTO.
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody BatchClassYearSemesterDTO dto) {
        try {
            BatchClassYearSemesterDTO created = batchClassYearSemesterService.createBatchClassYearSemester(dto);
            return ResponseEntity.ok(created);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Retrieves a batch-class-year-semester by ID.
     * @param id The batch-class-year-semester ID.
     * @return The BatchClassYearSemesterDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            BatchClassYearSemesterDTO dto = batchClassYearSemesterService.getBatchClassYearSemester(id);
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Retrieves all batch-class-year-semester combinations.
     * @return List of BatchClassYearSemesterDTOs.
     */
    @GetMapping
    public ResponseEntity<List<BatchClassYearSemesterDTO>> getAll() {
        return ResponseEntity.ok(batchClassYearSemesterService.getAllBatchClassYearSemesters());
    }

    /**
     * Updates a batch-class-year-semester combination.
     * @param id The batch-class-year-semester ID.
     * @param dto The updated data.
     * @return The updated BatchClassYearSemesterDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BatchClassYearSemesterDTO dto) {
        try {
            BatchClassYearSemesterDTO updated = batchClassYearSemesterService.updateBatchClassYearSemester(id, dto);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Assigns a grading system to a batch-class-year-semester.
     * @param id The batch-class-year-semester ID.
     * @param gradingSystemId The grading system ID.
     * @return No content on success.
     */
    @PutMapping("/{id}/grading-system/{gradingSystemId}")
    public ResponseEntity<?> assignGradingSystem(@PathVariable Long id, @PathVariable Long gradingSystemId) {
        try {
            batchClassYearSemesterService.assignGradingSystem(id, gradingSystemId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Deletes a batch-class-year-semester combination.
     * @param id The batch-class-year-semester ID.
     * @return No content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            batchClassYearSemesterService.deleteBatchClassYearSemester(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    // Explanation: RESTful controller for BatchClassYearSemester CRUD operations.
    // Why: Extends existing assignGradingSystem endpoint; provides full CRUD; handles errors with appropriate HTTP statuses.
}