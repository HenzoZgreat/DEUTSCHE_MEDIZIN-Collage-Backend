package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Service.BatchClassYearSemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bcsy")
public class BatchClassYearSemesterController {

    @Autowired
    private BatchClassYearSemesterService batchService;

    /**
     * Assigns a grading system to a batch.
     * @param batchId The batch ID.
     * @param gradingSystemId The grading system ID.
     * @return No content on success.
     */
    @PutMapping("/{batchId}/grading-system/{gradingSystemId}")
    public ResponseEntity<?> assignGradingSystem(@PathVariable Long batchId, @PathVariable Long gradingSystemId) {
        try {
            batchService.assignGradingSystem(batchId, gradingSystemId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    // Explanation: Endpoint to assign grading system to a batch.
    // Why: Provides admin control for updates; secured and error-handled.
    // Additional endpoints: For other batch operations as per existing system.
}
