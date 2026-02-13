package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.ProgressionSequenceDTO;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.ProgressionSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrar/progression-sequences")
@RequiredArgsConstructor
public class ProgressionSequenceController {

    private final ProgressionSequenceService progressionSequenceService;

    // ----------- Bulk Create (new response format) -----------
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBulk(@RequestBody List<Map<String, Object>> requests) {
        try {
            Map<String, Object> result = progressionSequenceService.createBulk(requests);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("totalRequested", 0);
            errorResponse.put("totalFailed", 1);
            Map<String, Object> err = new HashMap<>();
            err.put("input", "bulk request");
            err.put("reason", "Server error: " + e.getMessage());
            errorResponse.put("results", List.of(err));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ----------- Get All (with optional filters) -----------
    @GetMapping
    public ResponseEntity<List<ProgressionSequenceDTO>> getAll(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "true") boolean sortBySequence) {
        try {
            List<ProgressionSequenceDTO> result = progressionSequenceService.getAll(departmentId, sortBySequence);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch progression sequences: " + e.getMessage(), e);
        }
    }

    // ----------- Update One -----------
    @PutMapping("/{id}")
    public ResponseEntity<ProgressionSequenceDTO> updateOne(
            @PathVariable Long id,
            @RequestBody ProgressionSequenceDTO dto) {
        try {
            ProgressionSequenceDTO updated = progressionSequenceService.updateOne(id, dto);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update progression sequence: " + e.getMessage(), e);
        }
    }

    // ----------- Bulk Update -----------
    @PutMapping("/bulk")
    public ResponseEntity<List<ProgressionSequenceDTO>> updateBulk(@RequestBody List<ProgressionSequenceDTO> dtos) {
        try {
            List<ProgressionSequenceDTO> updated = progressionSequenceService.updateBulk(dtos);
            return ResponseEntity.ok(updated);
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to bulk update progression sequences: " + e.getMessage(), e);
        }
    }

    // ----------- Bulk Delete -----------
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteBulk(@RequestBody List<Long> ids) {
        try {
            progressionSequenceService.deleteBulk(ids);
            return ResponseEntity.noContent().build();
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to bulk delete progression sequences: " + e.getMessage(), e);
        }
    }
}
