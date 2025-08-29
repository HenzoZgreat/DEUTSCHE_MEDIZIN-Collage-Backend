package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.Entity.BatchClassYearSemester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.GradingSystem;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.BatchClassYearSemesterRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.GradingSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchClassYearSemesterService {

    @Autowired
    private BatchClassYearSemesterRepo batchClassYearSemesterRepository;
    @Autowired
    private GradingSystemRepository gradingSystemRepository;

    public void saveAll(List<BatchClassYearSemester> combinations) {
        batchClassYearSemesterRepository.saveAll(combinations);
    }

    /**
     * Assigns a grading system to a batch.
     * @param batchId The batch ID.
     * @param gradingSystemId The grading system ID.
     * @throws ResourceNotFoundException if batch or grading system not found.
     */
    public void assignGradingSystem(Long batchId, Long gradingSystemId) {
        BatchClassYearSemester batch = batchClassYearSemesterRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));

        GradingSystem gs = gradingSystemRepository.findById(gradingSystemId)
                .orElseThrow(() -> new ResourceNotFoundException("GradingSystem not found with id: " + gradingSystemId));

        batch.setGradingSystem(gs);
        batchClassYearSemesterRepository.save(batch);
    }

}