package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.EnrollmentTypeDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.EnrollmentType;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.EnrollmentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment-type")
public class EnrollmentTypeController {

    @Autowired
    private EnrollmentTypeService enrollmentTypeService;

    @PostMapping
    public ResponseEntity<EnrollmentType> createEnrollmentType(@RequestBody EnrollmentType enrollmentType) {
        try {
            if (enrollmentTypeService.existsByEnrollmentTypeCode(enrollmentType.getEnrollmentTypeCode())) {
                throw new DataIntegrityViolationException("EnrollmentType with code " + enrollmentType.getEnrollmentTypeCode() + " already exists");
            }
            EnrollmentType savedEnrollmentType = enrollmentTypeService.save(enrollmentType);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEnrollmentType);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<EnrollmentType>> createMultipleEnrollmentTypes(@RequestBody List<EnrollmentType> enrollmentTypes) {
        try {
            List<EnrollmentType> savedEnrollmentTypes = enrollmentTypeService.saveMultiple(enrollmentTypes);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEnrollmentTypes);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{enrollmentTypeCode}")
    public ResponseEntity<EnrollmentType> getEnrollmentTypeByCode(@PathVariable String enrollmentTypeCode) {
        try {
            EnrollmentType enrollmentType = enrollmentTypeService.findByEnrollmentTypeCode(enrollmentTypeCode);
            return ResponseEntity.ok(enrollmentType);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentType>> getAllEnrollmentTypes() {
        try {
            List<EnrollmentType> enrollmentTypes = enrollmentTypeService.findAll();
            return ResponseEntity.ok(enrollmentTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{enrollmentTypeCode}")
    public ResponseEntity<EnrollmentType> updateEnrollmentType(@PathVariable String enrollmentTypeCode, @RequestBody EnrollmentType enrollmentType) {
        try {
            EnrollmentType updatedEnrollmentType = enrollmentTypeService.update(enrollmentTypeCode, enrollmentType);
            return ResponseEntity.ok(updatedEnrollmentType);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{enrollmentTypeCode}")
    public ResponseEntity<Void> deleteEnrollmentType(@PathVariable String enrollmentTypeCode) {
        try {
            enrollmentTypeService.delete(enrollmentTypeCode);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}