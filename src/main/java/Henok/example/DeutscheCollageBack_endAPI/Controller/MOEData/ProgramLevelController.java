package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ProgramLevelCreateDto;
import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ProgramLevelUpdateDto;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramLevel;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.ProgramLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/program-levels")
@RequiredArgsConstructor
public class ProgramLevelController {

    private final ProgramLevelService service;

    // -------------------------------------------------- CREATE
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProgramLevelCreateDto dto) {
        try {
            ProgramLevel saved = service.create(dto);
            Map<String, Object> body = Map.of(
                    "message", "Program level created successfully",
                    "code", saved.getCode(),
                    "name", saved.getName()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }

    // -------------------------------------------------- UPDATE
    @PutMapping("/{code}")
    public ResponseEntity<?> update(@PathVariable String code,
                                    @RequestBody ProgramLevelUpdateDto dto) {
        try {
            ProgramLevel updated = service.update(code, dto);
            Map<String, Object> body = Map.of(
                    "message", "Program level updated successfully",
                    "code", updated.getCode(),
                    "name", updated.getName(),
                    "active", updated.getActive()
            );
            return ResponseEntity.ok(body);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }

    // ────── GET ALL ──────
    @GetMapping
    public ResponseEntity<List<ProgramLevel>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ────── GET BY CODE ──────
    @GetMapping("/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        try {
            ProgramLevel level = service.getByCode(code);
            return ResponseEntity.ok(level);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ────── DELETE (hard delete with usage check) ──────
    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteProgramLevel(@PathVariable String code) {
        try {
            service.deleteByCode(code);
            return ResponseEntity.ok(
                    Map.of("message", "Program level '" + code + "' deleted successfully")
            );
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete program level: " + e.getMessage()));
        }
    }
}