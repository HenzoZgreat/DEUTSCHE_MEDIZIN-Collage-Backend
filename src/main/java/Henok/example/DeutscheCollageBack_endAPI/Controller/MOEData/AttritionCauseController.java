package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.AttritionCauseDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AttritionCause;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.AttritionCauseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attrition-causes")
public class AttritionCauseController {

    @Autowired
    private AttritionCauseService attritionCauseService;

    @PostMapping
    public ResponseEntity<String> addAttritionCauses(@RequestBody List<AttritionCauseDTO> attritionCauseDTOs) {
        attritionCauseService.addAttritionCauses(attritionCauseDTOs);
        return ResponseEntity.ok("Attrition causes added successfully");
    }

    @GetMapping
    public ResponseEntity<List<AttritionCause>> getAllAttritionCauses() {
        return ResponseEntity.ok(attritionCauseService.getAllAttritionCauses());
    }
}