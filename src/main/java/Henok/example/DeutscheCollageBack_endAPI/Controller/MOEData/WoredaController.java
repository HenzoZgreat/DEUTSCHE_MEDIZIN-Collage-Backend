package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.WoredaDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Woreda;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.WoredaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/woredas")
public class WoredaController {

    @Autowired
    private WoredaService woredaService;

    @PostMapping
    public ResponseEntity<String> addWoredas(@RequestBody List<WoredaDTO> woredaDTOs) {
        woredaService.addWoredas(woredaDTOs);
        return ResponseEntity.ok("Woredas added successfully");
    }

    @GetMapping
    public ResponseEntity<List<Woreda>> getAllWoredas() {
        return ResponseEntity.ok(woredaService.getAllWoredas());
    }
}
