package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.BatchDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Batch;
import Henok.example.DeutscheCollageBack_endAPI.Repository.BatchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchService {

    @Autowired
    private BatchRepo batchRepository;

    public void addBatches(List<BatchDTO> batchDTOs) {
        List<Batch> batches = batchDTOs.stream()
                .map(dto -> new Batch(null, dto.getBatchName()))
                .collect(Collectors.toList());
        batchRepository.saveAll(batches);
    }

    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    public void addBatch(BatchDTO batchDTO) {
        Batch batch = new Batch(null, batchDTO.getBatchName());
        batchRepository.save(batch);
    }

    public Batch getBatchById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + id));
    }

    public void updateBatch(Long id, BatchDTO batchDTO) {
        Batch batch = getBatchById(id);
        batch.setBatchName(batchDTO.getBatchName());
        batchRepository.save(batch);
    }

    public void deleteBatch(Long id) {
        batchRepository.deleteById(id);
    }
}
