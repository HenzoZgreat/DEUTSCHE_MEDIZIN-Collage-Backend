package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.WoredaDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Woreda;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.WoredaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/woreda")
public class WoredaController {

    @Autowired
    private WoredaService woredaService;

    @PostMapping("/bulk")
    public ResponseEntity<List<Woreda>> addMultipleWoredas(@RequestBody List<Woreda> woredas) {
        try {
            List<Woreda> savedWoredas = woredaService.addMultipleWoredas(woredas);
            return ResponseEntity.ok(savedWoredas);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{woredaCode}")
    public ResponseEntity<Woreda> getWoredaByCode(@PathVariable String woredaCode) {
        try {
            Woreda woreda = woredaService.findByWoredaCode(woredaCode);
            return ResponseEntity.ok(woreda);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Woreda>> getAllWoredas() {
        try {
            List<Woreda> woredas = woredaService.findAll();
            return ResponseEntity.ok(woredas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
