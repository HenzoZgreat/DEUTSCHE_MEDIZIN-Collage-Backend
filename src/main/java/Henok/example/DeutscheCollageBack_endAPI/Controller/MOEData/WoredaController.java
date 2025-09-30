package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.WoredaDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Woreda;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.WoredaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/woreda")
public class WoredaController {

    @Autowired
    private WoredaService woredaService;

    // Add multiple woredas in bulk using DTO
    // Handles duplicates and validation errors
    @PostMapping("/bulk")
    public ResponseEntity<?> addMultipleWoredas(@RequestBody List<WoredaDTO> woredaDTOs) {
        try {
            List<WoredaDTO> savedWoredaDTOs = woredaService.addMultipleWoredas(woredaDTOs);
            return ResponseEntity.ok(savedWoredaDTOs);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Failed to add woredas: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid input: " + e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    // Retrieve a woreda by its code
    // Returns 404 if not found
    @GetMapping("/{woredaCode}")
    public ResponseEntity<?> getWoredaByCode(@PathVariable String woredaCode) {
        try {
            WoredaDTO woredaDTO = woredaService.findByWoredaCode(woredaCode);
            return ResponseEntity.ok(woredaDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve woreda: " + e.getMessage()));
        }
    }

    // Retrieve all woredas
    // Returns empty list if none found
    @GetMapping
    public ResponseEntity<?> getAllWoredas() {
        try {
            List<WoredaDTO> woredaDTOs = woredaService.findAll();
            return ResponseEntity.ok(woredaDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve woredas: " + e.getMessage()));
        }
    }

    // Filter woredas by zone code
    // Returns 404 if no woredas found for the zone
    @GetMapping("/zone/{zoneCode}")
    public ResponseEntity<?> getWoredasByZoneCode(@PathVariable String zoneCode) {
        try {
            List<WoredaDTO> woredaDTOs = woredaService.findByZoneCode(zoneCode);
            return ResponseEntity.ok(woredaDTOs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve woredas by zone: " + e.getMessage()));
        }
    }

    // Filter woredas by region code
    // Returns 404 if no woredas found for the region
    @GetMapping("/region/{regionCode}")
    public ResponseEntity<?> getWoredasByRegionCode(@PathVariable String regionCode) {
        try {
            List<WoredaDTO> woredaDTOs = woredaService.findByRegionCode(regionCode);
            return ResponseEntity.ok(woredaDTOs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve woredas by region: " + e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllWoredas() {
        try {
            woredaService.deleteAll();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete all woredas: " + e.getMessage()));
        }
    }
}