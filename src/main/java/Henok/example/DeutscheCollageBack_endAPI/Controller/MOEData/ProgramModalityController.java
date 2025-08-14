package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramModality;
import Henok.example.DeutscheCollageBack_endAPI.Service.ProgramModalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/program-modalities")
public class ProgramModalityController {

    @Autowired
    private ProgramModalityService programModalityService;

    @PostMapping
    public ResponseEntity<String> addProgramModalities(@RequestBody List<ProgramModality> programModalities) {
        programModalityService.addProgramModalities(programModalities);
        return ResponseEntity.ok("Program modalities added successfully");
    }

    @GetMapping
    public ResponseEntity<List<ProgramModality>> getAllProgramModalities() {
        return ResponseEntity.ok(programModalityService.getAllProgramModalities());
    }

    @PostMapping("/single")
    public ResponseEntity<String> addProgramModality(@RequestBody ProgramModality programModality) {
        programModalityService.addProgramModality(programModality);
        return ResponseEntity.ok("Program modality added successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramModality> getProgramModalityById(@PathVariable String id) {
        return ResponseEntity.ok(programModalityService.getProgramModalityById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProgramModality(@PathVariable String id, @RequestBody ProgramModality programModality) {
        programModalityService.updateProgramModality(id, programModality);
        return ResponseEntity.ok("Program modality updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProgramModality(@PathVariable String id) {
        programModalityService.deleteProgramModality(id);
        return ResponseEntity.ok("Program modality deleted successfully");
    }
}