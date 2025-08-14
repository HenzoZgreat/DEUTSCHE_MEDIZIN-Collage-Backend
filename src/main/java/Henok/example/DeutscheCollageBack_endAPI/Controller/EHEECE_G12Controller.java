package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.EHEECE_G12DTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.EHEECE_G12;
import Henok.example.DeutscheCollageBack_endAPI.Service.EHEECE_G12Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eheece-g12")
public class EHEECE_G12Controller {

    @Autowired
    private EHEECE_G12Service eheeceG12Service;

    @PostMapping
    public ResponseEntity<String> addEHEECE_G12s(@RequestBody List<EHEECE_G12DTO> dtos) {
        eheeceG12Service.addEHEECE_G12s(dtos);
        return ResponseEntity.ok("EHEECE_G12 entries added successfully");
    }

    @GetMapping
    public ResponseEntity<List<EHEECE_G12>> getAllEHEECE_G12() {
        return ResponseEntity.ok(eheeceG12Service.getAllEHEECE_G12());
    }

    @PostMapping("/single")
    public ResponseEntity<String> addEHEECE_G12(@RequestBody EHEECE_G12DTO dto) {
        eheeceG12Service.addEHEECE_G12(dto);
        return ResponseEntity.ok("EHEECE_G12 entry added successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<EHEECE_G12> getEHEECE_G12ById(@PathVariable Long id) {
        return ResponseEntity.ok(eheeceG12Service.getEHEECE_G12ById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEHEECE_G12(@PathVariable Long id, @RequestBody EHEECE_G12DTO dto) {
        eheeceG12Service.updateEHEECE_G12(id, dto);
        return ResponseEntity.ok("EHEECE_G12 entry updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEHEECE_G12(@PathVariable Long id) {
        eheeceG12Service.deleteEHEECE_G12(id);
        return ResponseEntity.ok("EHEECE_G12 entry deleted successfully");
    }
}