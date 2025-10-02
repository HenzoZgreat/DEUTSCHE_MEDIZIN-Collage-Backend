package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ImpairmentDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Impairment;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.ImpairmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// All methods delegate to service and catch exceptions for structured error responses
@RestController
@RequestMapping("/api/impairments")
public class ImpairmentController {

    @Autowired
    private ImpairmentService impairmentService;

    // Add multiple impairments in bulk using DTO
    // Handles validation, duplicates, and other errors
    @PostMapping
    public ResponseEntity<?> addImpairments(@RequestBody List<ImpairmentDTO> impairmentDTOs) {
        try {
            List<ImpairmentDTO> savedDTOs = impairmentService.addImpairments(impairmentDTOs);
            return ResponseEntity.ok(savedDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid input: " + e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Failed to add impairments: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    // Add a single impairment
    // Uses the same DTO for input, validates and saves
    @PostMapping("/single")
    public ResponseEntity<?> addSingleImpairment(@RequestBody ImpairmentDTO impairmentDTO) {
        try {
            ImpairmentDTO savedDTO = impairmentService.addSingleImpairment(impairmentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid input: " + e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Failed to add impairment: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    // Retrieve all impairments as DTOs
    // Returns empty list if none found
    @GetMapping
    public ResponseEntity<?> getAllImpairments() {
        try {
            List<ImpairmentDTO> impairments = impairmentService.getAllImpairments();
            return ResponseEntity.ok(impairments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve impairments: " + e.getMessage()));
        }
    }

    // Retrieve impairment by code
    // Returns 404 if not found
    @GetMapping("/{impairmentCode}")
    public ResponseEntity<?> getImpairmentByCode(@PathVariable String impairmentCode) {
        try {
            ImpairmentDTO impairmentDTO = impairmentService.getImpairmentByCode(impairmentCode);
            return ResponseEntity.ok(impairmentDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve impairment: " + e.getMessage()));
        }
    }

    // Delete impairment by code
    // Returns 404 if not found, no content on success
    @DeleteMapping("/{impairmentCode}")
    public ResponseEntity<?> removeImpairment(@PathVariable String impairmentCode) {
        try {
            impairmentService.removeImpairment(impairmentCode);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Cannot delete impairment due to dependencies: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete impairment: " + e.getMessage()));
        }
    }
}