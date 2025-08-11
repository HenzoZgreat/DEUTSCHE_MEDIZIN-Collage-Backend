package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;

import Henok.example.DeutscheCollageBack_endAPI.DTO.ZoneDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    @PostMapping
    public ResponseEntity<String> addZones(@RequestBody List<ZoneDTO> zoneDTOs) {
        zoneService.addZones(zoneDTOs);
        return ResponseEntity.ok("Zones added successfully");
    }

    @GetMapping
    public ResponseEntity<List<Zone>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }
}