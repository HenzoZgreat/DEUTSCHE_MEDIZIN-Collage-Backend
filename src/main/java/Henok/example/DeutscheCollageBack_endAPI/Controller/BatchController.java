package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.BatchDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Batch;
import Henok.example.DeutscheCollageBack_endAPI.Service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    @Autowired
    private BatchService batchService;

    @PostMapping
    public ResponseEntity<String> addBatches(@RequestBody List<BatchDTO> batchDTOs) {
        batchService.addBatches(batchDTOs);
        return ResponseEntity.ok("Batches added successfully");
    }

    @GetMapping
    public ResponseEntity<List<Batch>> getAllBatches() {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

    @PostMapping("/single")
    public ResponseEntity<String> addBatch(@RequestBody BatchDTO batchDTO) {
        batchService.addBatch(batchDTO);
        return ResponseEntity.ok("Batch added successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Batch> getBatchById(@PathVariable Long id) {
        return ResponseEntity.ok(batchService.getBatchById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBatch(@PathVariable Long id, @RequestBody BatchDTO batchDTO) {
        batchService.updateBatch(id, batchDTO);
        return ResponseEntity.ok("Batch updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return ResponseEntity.ok("Batch deleted successfully");
    }
}