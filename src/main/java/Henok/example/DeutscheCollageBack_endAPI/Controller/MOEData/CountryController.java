package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.CountryDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Country;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/country")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService service;

    @PostMapping("/bulk")
    public ResponseEntity<?> addMultiple(@RequestBody List<Country> countries) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.addMultiple(countries));
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add countries: " + e.getMessage()));
        }
    }

    @PostMapping("/single")
    public ResponseEntity<?> addSingle(@RequestBody Country country) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.addSingle(country));
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add country: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Country>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{countryCode}")
    public ResponseEntity<?> getByCode(@PathVariable String countryCode) {
        try {
            return ResponseEntity.ok(service.findByCode(countryCode));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve country: " + e.getMessage()));
        }
    }

    @PutMapping("/{countryCode}")
    public ResponseEntity<?> update(@PathVariable String countryCode,
                                    @RequestBody Country country) {
        try {
            return ResponseEntity.ok(service.update(countryCode, country));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update country: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{countryCode}")
    public ResponseEntity<?> delete(@PathVariable String countryCode) {
        try {
            service.delete(countryCode);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete country: " + e.getMessage()));
        }
    }
}