package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.AcademicYearDTO;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.AcademicYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService service;

    @PostMapping
    public ResponseEntity<?> addMultiple(@RequestBody List<AcademicYearDTO> dtos) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.addAcademicYears(dtos));
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add academic years: " + e.getMessage()));
        }
    }

    @PostMapping("/single")
    public ResponseEntity<?> addSingle(@RequestBody AcademicYearDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.addSingle(dto));
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add academic year: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<AcademicYearDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{yearCode}")
    public ResponseEntity<?> getByCode(@PathVariable String yearCode) {
        try {
            return ResponseEntity.ok(service.getByCode(yearCode));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve academic year: " + e.getMessage()));
        }
    }

    @PutMapping("/{yearCode}")
    public ResponseEntity<?> update(@PathVariable String yearCode,
                                    @RequestBody AcademicYearDTO dto) {
        try {
            return ResponseEntity.ok(service.update(yearCode, dto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update academic year: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{yearCode}")
    public ResponseEntity<?> delete(@PathVariable String yearCode) {
        try {
            service.delete(yearCode);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete academic year: " + e.getMessage()));
        }
    }
}