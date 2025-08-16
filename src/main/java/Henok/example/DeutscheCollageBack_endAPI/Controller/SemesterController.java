package Henok.example.DeutscheCollageBack_endAPI.Controller;


import Henok.example.DeutscheCollageBack_endAPI.DTO.SemesterDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;

    @PostMapping
    public ResponseEntity<String> addSemesters(@RequestBody List<SemesterDTO> semesterDTOs) {
        semesterService.addSemesters(semesterDTOs);
        return ResponseEntity.ok("Semesters added successfully");
    }

    @GetMapping
    public ResponseEntity<List<Semester>> getAllSemesters() {
        return ResponseEntity.ok(semesterService.getAllSemesters());
    }
}