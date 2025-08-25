package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.CountryDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Country;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/country")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @PostMapping("/bulk")
    public ResponseEntity<List<Country>> addMultipleCountries(@RequestBody List<Country> countries) {
        try {
            List<Country> savedCountries = countryService.addMultipleCountries(countries);
            return ResponseEntity.ok(savedCountries);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{countryCode}")
    public ResponseEntity<Country> getCountryByCode(@PathVariable String countryCode) {
        try {
            Country country = countryService.findByCountryCode(countryCode);
            return ResponseEntity.ok(country);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Country>> getAllCountries() {
        try {
            List<Country> countries = countryService.findAll();
            return ResponseEntity.ok(countries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}