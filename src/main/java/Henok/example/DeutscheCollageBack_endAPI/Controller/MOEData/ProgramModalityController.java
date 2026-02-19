package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ProgramModalityDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramModality;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.ProgramModalityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/program-modality")
@RequiredArgsConstructor
public class ProgramModalityController {

    @Autowired
    private ProgramModalityService programModalityService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProgramModalityDTO dto) {
        try {
            ProgramModalityDTO saved = programModalityService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to create program modality: " + e.getMessage()));
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> createMultiple(@RequestBody List<ProgramModalityDTO> dtos) {
        try {
            List<ProgramModalityDTO> saved = programModalityService.saveMultiple(dtos);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to create program modalities: " + e.getMessage()));
        }
    }

    @GetMapping("/{modalityCode}")
    public ResponseEntity<?> getByCode(@PathVariable String modalityCode) {
        try {
            ProgramModalityDTO dto = programModalityService.findByModalityCode(modalityCode);
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve program modality: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<ProgramModalityDTO>> getAll() {
        return ResponseEntity.ok(programModalityService.findAll());
    }

    @GetMapping("/level/{programLevelCode}")
    public ResponseEntity<?> getByProgramLevel(@PathVariable String programLevelCode) {
        try {
            List<ProgramModalityDTO> modalities = programModalityService.findByProgramLevelCode(programLevelCode);
            return ResponseEntity.ok(modalities);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve modalities for program level: " + e.getMessage()));
        }
    }

    @PutMapping("/{modalityCode}")
    public ResponseEntity<?> update(@PathVariable String modalityCode, @RequestBody ProgramModalityDTO dto) {
        try {
            ProgramModalityDTO updated = programModalityService.update(modalityCode, dto);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update program modality: " + e.getMessage()));
        }
    }

    /**
     * Hard delete of a program modality.
     * Deletion is blocked if modality is still referenced by students or departments.
     */
    @DeleteMapping("/{modalityCode}")
    public ResponseEntity<?> deleteProgramModality(@PathVariable String modalityCode) {
        try {
            programModalityService.delete(modalityCode);

            // 204 No Content is standard for successful DELETE with no body
            return ResponseEntity.noContent().build();

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));

        } catch (BadRequestException e) {
            // 409 Conflict or 400 Bad Request — both acceptable
            // 409 is often used when resource state prevents operation
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));

        } catch (DataIntegrityViolationException e) {
            // In case some unexpected foreign key constraint still blocks
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(
                            "Cannot delete modality due to database constraints. " +
                                    "It may still be referenced in other tables."
                    ));

        } catch (Exception e) {
            // Catch-all for unexpected errors (logging should happen here in real app)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete program modality: " + e.getMessage()));
        }
    }
}