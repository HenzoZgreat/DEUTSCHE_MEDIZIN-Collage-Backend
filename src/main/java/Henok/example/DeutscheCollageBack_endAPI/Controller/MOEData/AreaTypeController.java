package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.AreaTypeDTO;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.AreaTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/area-types")
@RequiredArgsConstructor
public class AreaTypeController {

    private final AreaTypeService service;

    @PostMapping
    public ResponseEntity<?> addMultiple(@RequestBody List<AreaTypeDTO> dtos) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.addAreaTypes(dtos));
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add area types: " + e.getMessage()));
        }
    }

    @PostMapping("/single")
    public ResponseEntity<?> addSingle(@RequestBody AreaTypeDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.addSingle(dto));
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add area type: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<AreaTypeDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        try {
            return ResponseEntity.ok(service.getByCode(code));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve area type: " + e.getMessage()));
        }
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> update(@PathVariable String code,
                                    @RequestBody AreaTypeDTO dto) {
        try {
            return ResponseEntity.ok(service.update(code, dto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update area type: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> delete(@PathVariable String code) {
        try {
            service.delete(code);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete area type: " + e.getMessage()));
        }
    }
}