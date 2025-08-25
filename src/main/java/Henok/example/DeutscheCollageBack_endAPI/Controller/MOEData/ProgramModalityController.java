package Henok.example.DeutscheCollageBack_endAPI.Controller.MOEData;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramModality;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices.ProgramModalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/program-modality")
public class ProgramModalityController {

    @Autowired
    private ProgramModalityService programModalityService;

    @PostMapping
    public ResponseEntity<ProgramModality> createProgramModality(@RequestBody ProgramModality programModality) {
        try {
            if (programModalityService.existsByModalityCode(programModality.getModalityCode())) {
                throw new DataIntegrityViolationException("ProgramModality with code " + programModality.getModalityCode() + " already exists");
            }
            ProgramModality savedProgramModality = programModalityService.save(programModality);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProgramModality);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<ProgramModality>> createMultipleProgramModalities(@RequestBody List<ProgramModality> programModalities) {
        try {
            List<ProgramModality> savedProgramModalities = programModalityService.saveMultiple(programModalities);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProgramModalities);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{modalityCode}")
    public ResponseEntity<ProgramModality> getProgramModalityByCode(@PathVariable String modalityCode) {
        try {
            ProgramModality programModality = programModalityService.findByModalityCode(modalityCode);
            return ResponseEntity.ok(programModality);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<ProgramModality>> getAllProgramModalities() {
        try {
            List<ProgramModality> programModalities = programModalityService.findAll();
            return ResponseEntity.ok(programModalities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{modalityCode}")
    public ResponseEntity<ProgramModality> updateProgramModality(@PathVariable String modalityCode, @RequestBody ProgramModality programModality) {
        try {
            ProgramModality updatedProgramModality = programModalityService.update(modalityCode, programModality);
            return ResponseEntity.ok(updatedProgramModality);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{modalityCode}")
    public ResponseEntity<Void> deleteProgramModality(@PathVariable String modalityCode) {
        try {
            programModalityService.delete(modalityCode);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}