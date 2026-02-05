package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.BatchClassYearSemesterDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.BatchClassYearSemester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Entity.DepartmentBCYS;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentBCYSRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.BatchClassYearSemesterRepo;
import Henok.example.DeutscheCollageBack_endAPI.Service.BatchClassYearSemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bcsy")
public class BatchClassYearSemesterController {

    @Autowired
    private BatchClassYearSemesterService batchClassYearSemesterService;
    @Autowired
    private BatchClassYearSemesterRepo bcysRepository;
    @Autowired
    private DepartmentRepo departmentRepository;
    @Autowired
    private DepartmentBCYSRepository departmentBCYSRepository;

    /**
     * Creates a new batch-class-year-semester combination.
     * @param dto The batch-class-year-semester data.
     * @return The created BatchClassYearSemesterDTO.
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody BatchClassYearSemesterDTO dto) {
        try {
            BatchClassYearSemesterDTO created = batchClassYearSemesterService.createBatchClassYearSemester(dto);
            return ResponseEntity.ok(created);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Retrieves a batch-class-year-semester by ID.
     * @param id The batch-class-year-semester ID.
     * @return The BatchClassYearSemesterDTO.
     */
    //git
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            BatchClassYearSemesterDTO dto = batchClassYearSemesterService.getBatchClassYearSemester(id);
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Retrieves all batch-class-year-semester combinations.
     * @return List of BatchClassYearSemesterDTOs.
     */
    @GetMapping
    public ResponseEntity<List<BatchClassYearSemesterDTO>> getAll() {
        return ResponseEntity.ok(batchClassYearSemesterService.getAllBatchClassYearSemesters());
    }

    /**
     * Updates a batch-class-year-semester combination.
     * @param id The batch-class-year-semester ID.
     * @param dto The updated data.
     * @return The updated BatchClassYearSemesterDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BatchClassYearSemesterDTO dto) {
        try {
            BatchClassYearSemesterDTO updated = batchClassYearSemesterService.updateBatchClassYearSemester(id, dto);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }


    /**
     * Deletes a batch-class-year-semester combination.
     * @param id The batch-class-year-semester ID.
     * @return No content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Map<String, Object> result = batchClassYearSemesterService.deleteBatchClassYearSemester(id);
            return ResponseEntity.ok(result);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    // Explanation: RESTful controller for BatchClassYearSemester CRUD operations.
    // Why: Extends existing assignGradingSystem endpoint; provides full CRUD; handles errors with appropriate HTTP statuses.


    /**
     * One-time migration endpoint:
     * Creates DepartmentBCYS entries for EVERY existing BatchClassYearSemester
     * linked to EVERY department in the system.
     *
     * Leaves academicYear, classStart*, classEnd* fields as NULL.
     * Skips already existing links (idempotent).
     *
     * @return simple map with summary of what was done
     */
    @PostMapping("/populate-department-bcys")
    public ResponseEntity<Map<String, Object>> populateDepartmentBCYSForAll() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<BatchClassYearSemester> allBcys = bcysRepository.findAll();
            if (allBcys.isEmpty()) {
                response.put("status", "info");
                response.put("message", "No BatchClassYearSemester records found. Nothing to migrate.");
                return ResponseEntity.ok(response);
            }

            List<Department> allDepartments = departmentRepository.findAll();
            if (allDepartments.isEmpty()) {
                response.put("status", "warning");
                response.put("message", "No departments found. Migration skipped.");
                return ResponseEntity.ok(response);
            }

            // Find Medicine department (id = 2)
            Department medicineDept = allDepartments.stream()
                    .filter(d -> d.getDptID() == 2L)
                    .findFirst()
                    .orElse(null);

            int totalCreated = 0;
            int totalSkipped = 0;
            int totalSkippedRules = 0;
            List<String> errors = new ArrayList<>();

            for (BatchClassYearSemester bcys : allBcys) {
                String batchName = bcys.getBatch() != null ? bcys.getBatch().getBatchName() : null;
                String classYearName = bcys.getClassYear() != null ? bcys.getClassYear().getClassYear() : null;

                // Rule 1: batch name == "0" → skip completely
                if ("0".equals(batchName)) {
                    totalSkippedRules++;
                    continue;
                }

                // Determine which departments to map
                List<Department> departmentsToMap;

                if ("PC1".equals(classYearName) || "PC2".equals(classYearName) ||
                        "C1".equals(classYearName) || "C2".equals(classYearName) || "C3".equals(classYearName)) {
                    // Rule 3: PC1/PC2/C1/C2/C3 → ONLY Medicine
                    departmentsToMap = medicineDept != null ? List.of(medicineDept) : Collections.emptyList();
                } else if (classYearName != null && classYearName.matches("\\d+") && Integer.parseInt(classYearName) >= 2) {
                    // Rule 2: class year >= 2 → exclude Medicine
                    departmentsToMap = allDepartments.stream()
                            .filter(d -> d.getDptID() != 2L)
                            .collect(Collectors.toList());
                } else {
                    // Default: map to all departments
                    departmentsToMap = allDepartments;
                }

                if (departmentsToMap.isEmpty()) {
                    totalSkippedRules++;
                    continue;
                }

                for (Department dept : departmentsToMap) {
                    boolean exists = departmentBCYSRepository.existsByBcysAndDepartment(bcys, dept);
                    if (exists) {
                        totalSkipped++;
                        continue;
                    }

                    try {
                        DepartmentBCYS link = new DepartmentBCYS();
                        link.setBcys(bcys);
                        link.setDepartment(dept);
                        // All other fields remain null

                        departmentBCYSRepository.save(link);
                        totalCreated++;
                    } catch (Exception e) {
                        String errorMsg = String.format("Failed for bcysID=%d + deptID=%d: %s",
                                bcys.getBcysID(), dept.getDptID(), e.getMessage());
                        errors.add(errorMsg);
                    }
                }
            }

            response.put("status", errors.isEmpty() ? "success" : "partial-success");
            response.put("message", "Migration completed with rules applied");
            response.put("bcysProcessed", allBcys.size());
            response.put("departmentsTotal", allDepartments.size());
            response.put("linksCreated", totalCreated);
            response.put("linksSkippedAlreadyExist", totalSkipped);
            response.put("bcysSkippedByRules", totalSkippedRules);
            response.put("errors", errors);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Migration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}