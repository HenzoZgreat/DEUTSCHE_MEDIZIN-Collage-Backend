package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ZoneDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zone")
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    @PostMapping("/bulk")
    public ResponseEntity<List<Zone>> addMultipleZones(@RequestBody List<Zone> zones) {
        try {
            List<Zone> savedZones = zoneService.addMultipleZones(zones);
            return ResponseEntity.ok(savedZones);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{zoneCode}")
    public ResponseEntity<Zone> getZoneByCode(@PathVariable String zoneCode) {
        try {
            Zone zone = zoneService.findByZoneCode(zoneCode);
            return ResponseEntity.ok(zone);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Zone>> getAllZones() {
        try {
            List<Zone> zones = zoneService.findAll();
            return ResponseEntity.ok(zones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}