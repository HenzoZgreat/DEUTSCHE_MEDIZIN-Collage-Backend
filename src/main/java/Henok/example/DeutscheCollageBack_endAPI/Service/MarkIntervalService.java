package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MarkIntervalDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.GradingSystem;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MarkInterval;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.GradingSystemRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MarkIntervalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarkIntervalService {

    @Autowired
    private MarkIntervalRepository markIntervalRepository;

    @Autowired
    private GradingSystemRepository gradingSystemRepository;

    /**
     * Creates a new mark interval for a grading system.
     * @param gradingSystemId The ID of the grading system.
     * @param dto The mark interval data.
     * @return The created MarkIntervalDTO.
     * @throws ResourceNotFoundException if grading system not found.
     * @throws IllegalArgumentException if interval is invalid.
     */
    public MarkIntervalDTO createMarkInterval(Long gradingSystemId, MarkIntervalDTO dto) {
        GradingSystem gradingSystem = gradingSystemRepository.findById(gradingSystemId)
                .orElseThrow(() -> new ResourceNotFoundException("GradingSystem not found with id: " + gradingSystemId));

        MarkInterval interval = new MarkInterval();
        interval.setDescription(dto.getDescription());
        interval.setMin(dto.getMin());
        interval.setMax(dto.getMax());
        interval.setGivenValue(dto.getGivenValue());
        interval.setGradeLetter(dto.getGradeLetter());
        interval.setGradingSystem(gradingSystem);

        // Validate single interval
        validateSingleInterval(interval);

        // Validate against existing intervals in the grading system
        List<MarkInterval> existingIntervals = markIntervalRepository.findByGradingSystemId(gradingSystemId);
        existingIntervals.add(interval);
        validateIntervals(existingIntervals);

        MarkInterval saved = markIntervalRepository.save(interval);
        return toDTO(saved);
    }

    /**
     * Retrieves a mark interval by ID.
     * @param id The mark interval ID.
     * @return The MarkIntervalDTO.
     * @throws ResourceNotFoundException if not found.
     */
    public MarkIntervalDTO getMarkInterval(Long id) {
        MarkInterval interval = markIntervalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MarkInterval not found with id: " + id));
        return toDTO(interval);
    }

    /**
     * Retrieves all mark intervals for a grading system.
     * @param gradingSystemId The grading system ID.
     * @return List of MarkIntervalDTOs.
     * @throws ResourceNotFoundException if grading system not found.
     */
    public List<MarkIntervalDTO> getAllByGradingSystem(Long gradingSystemId) {
        gradingSystemRepository.findById(gradingSystemId)
                .orElseThrow(() -> new ResourceNotFoundException("GradingSystem not found with id: " + gradingSystemId));
        return markIntervalRepository.findByGradingSystemId(gradingSystemId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates a mark interval.
     * @param id The mark interval ID.
     * @param dto The updated data.
     * @return The updated MarkIntervalDTO.
     * @throws ResourceNotFoundException if not found.
     * @throws IllegalArgumentException if interval is invalid.
     */
    public MarkIntervalDTO updateMarkInterval(Long id, MarkIntervalDTO dto) {
        MarkInterval interval = markIntervalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MarkInterval not found with id: " + id));

        // Update fields
        interval.setDescription(dto.getDescription());
        interval.setMin(dto.getMin());
        interval.setMax(dto.getMax());
        interval.setGivenValue(dto.getGivenValue());
        interval.setGradeLetter(dto.getGradeLetter());

        // Validate single interval
        validateSingleInterval(interval);

        // Validate against other intervals in the same grading system
        List<MarkInterval> existingIntervals = markIntervalRepository.findByGradingSystemId(interval.getGradingSystem().getId());
        existingIntervals.removeIf(existing -> existing.getId().equals(id)); // Exclude current interval
        existingIntervals.add(interval);
        validateIntervals(existingIntervals);

        MarkInterval updated = markIntervalRepository.save(interval);
        return toDTO(updated);
    }

    /**
     * Deletes a mark interval by ID.
     * @param id The mark interval ID.
     * @throws ResourceNotFoundException if not found.
     */
    public void deleteMarkInterval(Long id) {
        MarkInterval interval = markIntervalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MarkInterval not found with id: " + id));
        markIntervalRepository.delete(interval);
    }

    /**
     * Validates a single interval for basic constraints.
     * @param interval The interval to validate.
     * @throws IllegalArgumentException if invalid.
     */
    private void validateSingleInterval(MarkInterval interval) {
        if (interval.getMin() >= interval.getMax()) {
            throw new IllegalArgumentException("Invalid interval: min must be less than max");
        }
        if (interval.getGivenValue() < 0) {
            throw new IllegalArgumentException("Invalid GPA value: must be non-negative");
        }
        if (interval.getGradeLetter() == null || interval.getGradeLetter().isEmpty()) {
            throw new IllegalArgumentException("Grade letter cannot be empty");
        }
    }

    /**
     * Validates a list of intervals for overlaps.
     * @param intervals List of intervals to validate.
     * @throws IllegalArgumentException if overlapping.
     */
    private void validateIntervals(List<MarkInterval> intervals) {
        intervals.sort(Comparator.comparingDouble(MarkInterval::getMin));
        for (int i = 0; i < intervals.size() - 1; i++) {
            MarkInterval current = intervals.get(i);
            MarkInterval next = intervals.get(i + 1);
            if (current.getMax() >= next.getMin()) {
                throw new IllegalArgumentException("Overlapping intervals detected");
            }
        }
    }

    /**
     * Converts MarkInterval entity to DTO.
     * @param interval The MarkInterval entity.
     * @return The MarkIntervalDTO.
     */
    private MarkIntervalDTO toDTO(MarkInterval interval) {
        return new MarkIntervalDTO(
                interval.getId(),
                interval.getDescription(),
                interval.getMin(),
                interval.getMax(),
                interval.getGivenValue(),
                interval.getGradeLetter()
        );
    }

    // Explanation: Service handles CRUD for MarkInterval with validation.
    // Why: Ensures valid intervals within a grading system; handles errors appropriately.
}
