package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ZoneDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zone")
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    // Add multiple zones in bulk using DTO
    // Handles duplicates and validation errors
    @PostMapping("/bulk")
    public ResponseEntity<?> addMultipleZones(@RequestBody List<ZoneDTO> zoneDTOs) {
        try {
            List<ZoneDTO> savedZoneDTOs = zoneService.addMultipleZones(zoneDTOs);
            return ResponseEntity.ok(savedZoneDTOs);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Failed to add zones: " + e.getMessage()));
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

    // Retrieve a zone by its code
    // Returns 404 if not found
    @GetMapping("/{zoneCode}")
    public ResponseEntity<?> getZoneByCode(@PathVariable String zoneCode) {
        try {
            ZoneDTO zoneDTO = zoneService.findByZoneCode(zoneCode);
            return ResponseEntity.ok(zoneDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve zone: " + e.getMessage()));
        }
    }

    // Retrieve all zones
    // Returns empty list if none found
    @GetMapping
    public ResponseEntity<?> getAllZones() {
        try {
            List<ZoneDTO> zoneDTOs = zoneService.findAll();
            return ResponseEntity.ok(zoneDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve zones: " + e.getMessage()));
        }
    }

    // Filter zones by region code
    // Returns 404 if no zones found for the region
    @GetMapping("/region/{regionCode}")
    public ResponseEntity<?> getZonesByRegionCode(@PathVariable String regionCode) {
        try {
            List<ZoneDTO> zoneDTOs = zoneService.findByRegionCode(regionCode);
            return ResponseEntity.ok(zoneDTOs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve zones by region: " + e.getMessage()));
        }
    }
}