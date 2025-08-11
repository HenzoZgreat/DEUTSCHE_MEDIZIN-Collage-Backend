package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentStatusDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
public class StudentStatusController {

    @Autowired
    private StudentStatusService statusService;

    @PostMapping
    public ResponseEntity<String> addStatuses(@RequestBody List<StudentStatusDTO> statusDTOs) {
        statusService.addStatuses(statusDTOs);
        return ResponseEntity.ok("Statuses added successfully");
    }

    @GetMapping
    public ResponseEntity<List<StudentStatus>> getAllStatuses() {
        return ResponseEntity.ok(statusService.getAllStatuses());
    }

    @PostMapping("/single")
    public ResponseEntity<String> addStatus(@RequestBody StudentStatusDTO statusDTO) {
        statusService.addStatus(statusDTO);
        return ResponseEntity.ok("Status added successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentStatus> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(statusService.getStatusById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestBody StudentStatusDTO statusDTO) {
        statusService.updateStatus(id, statusDTO);
        return ResponseEntity.ok("Status updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStatus(@PathVariable Long id) {
        statusService.deleteStatus(id);
        return ResponseEntity.ok("Status deleted successfully");
    }
}