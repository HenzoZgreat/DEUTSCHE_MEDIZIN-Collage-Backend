package Henok.example.DeutscheCollageBack_endAPI.Controller;


import Henok.example.DeutscheCollageBack_endAPI.DTO.EnrollmentTypeDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.EnrollmentType;
import Henok.example.DeutscheCollageBack_endAPI.Service.EnrollmentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment-types")
public class EnrollmentTypeController {

    @Autowired
    private EnrollmentTypeService enrollmentTypeService;

    @PostMapping
    public ResponseEntity<String> addEnrollmentTypes(@RequestBody List<EnrollmentTypeDTO> enrollmentTypeDTOs) {
        enrollmentTypeService.addEnrollmentTypes(enrollmentTypeDTOs);
        return ResponseEntity.ok("Enrollment types added successfully");
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentType>> getAllEnrollmentTypes() {
        return ResponseEntity.ok(enrollmentTypeService.getAllEnrollmentTypes());
    }
}