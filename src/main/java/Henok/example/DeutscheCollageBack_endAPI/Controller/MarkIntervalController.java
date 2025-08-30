package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MarkIntervalDTO;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MarkIntervalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mark-intervals")
public class MarkIntervalController {

    @Autowired
    private MarkIntervalService markIntervalService;

    /**
     * Creates a new mark interval for a grading system.
     * @param gradingSystemId The ID of the grading system.
     * @param dto The mark interval data.
     * @return The created mark interval DTO.
     */
    @PostMapping("/grading-system/{gradingSystemId}")
    public ResponseEntity<?> create(@PathVariable Long gradingSystemId, @RequestBody MarkIntervalDTO dto) {
        try {
            MarkIntervalDTO created = markIntervalService.createMarkInterval(gradingSystemId, dto);
            return ResponseEntity.ok(created);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Retrieves a mark interval by ID.
     * @param id The mark interval ID.
     * @return The mark interval DTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            MarkIntervalDTO dto = markIntervalService.getMarkInterval(id);
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Retrieves all mark intervals for a grading system.
     * @param gradingSystemId The ID of the grading system.
     * @return List of mark interval DTOs.
     */
    @GetMapping("/grading-system/{gradingSystemId}")
    public ResponseEntity<?> getAllByGradingSystem(@PathVariable Long gradingSystemId) {
        try {
            return ResponseEntity.ok(markIntervalService.getAllByGradingSystem(gradingSystemId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Updates a mark interval.
     * @param id The mark interval ID.
     * @param dto The updated mark interval data.
     * @return The updated mark interval DTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MarkIntervalDTO dto) {
        try {
            MarkIntervalDTO updated = markIntervalService.updateMarkInterval(id, dto);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Deletes a mark interval.
     * @param id The mark interval ID.
     * @return No content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            markIntervalService.deleteMarkInterval(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    // Explanation: RESTful controller for MarkInterval CRUD operations.
    // Why: Delegates to service; handles errors with appropriate HTTP statuses and ErrorResponse; no @PreAuthorize as per request.
}