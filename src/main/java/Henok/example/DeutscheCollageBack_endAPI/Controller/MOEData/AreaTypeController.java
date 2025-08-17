package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.AreaTypeDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AreaType;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.AreaTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/area-types")
public class AreaTypeController {

    @Autowired
    private AreaTypeService areaTypeService;

    @PostMapping
    public ResponseEntity<String> addAreaTypes(@RequestBody List<AreaTypeDTO> areaTypeDTOs) {
        areaTypeService.addAreaTypes(areaTypeDTOs);
        return ResponseEntity.ok("Area types added successfully");
    }

    @GetMapping
    public ResponseEntity<List<AreaType>> getAllAreaTypes() {
        return ResponseEntity.ok(areaTypeService.getAllAreaTypes());
    }
}