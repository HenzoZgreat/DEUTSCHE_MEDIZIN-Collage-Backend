package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;


import Henok.example.DeutscheCollageBack_endAPI.DTO.ImpairmentDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Impairment;
import Henok.example.DeutscheCollageBack_endAPI.Service.ImpairmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/impairments")
public class ImpairmentController {

    @Autowired
    private ImpairmentService impairmentService;

    @PostMapping
    public ResponseEntity<String> addImpairments(@RequestBody List<ImpairmentDTO> impairmentDTOs) {
        impairmentService.addImpairments(impairmentDTOs);
        return ResponseEntity.ok("Impairments added successfully");
    }

    @GetMapping
    public ResponseEntity<List<Impairment>> getAllImpairments() {
        return ResponseEntity.ok(impairmentService.getAllImpairments());
    }
}