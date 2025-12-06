package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.GradingSystemDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.MarkIntervalDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Entity.GradingSystem;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MarkInterval;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.GradingSystemRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MarkIntervalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradingSystemService {

    @Autowired
    private GradingSystemRepository gradingSystemRepository;

    @Autowired
    private DepartmentRepo departmentRepository; // Assume exists for fetching departments

    @Autowired
    private MarkIntervalRepository markIntervalRepository;

    /**
     * Creates a new grading system with its intervals, optionally tied to a department.
     * Validates version name uniqueness and interval ranges.
     * @param dto The input DTO containing grading system details.
     * @return The created GradingSystemDTO.
     * @throws IllegalArgumentException if version name exists or intervals are invalid.
     * @throws ResourceNotFoundException if departmentId provided but not found.
     */
    public GradingSystemDTO createGradingSystem(GradingSystemDTO dto) {
        // Check for duplicate version name
        if (gradingSystemRepository.findByVersionName(dto.getVersionName()).isPresent()) {
            throw new IllegalArgumentException("Version name already exists");
        }

        // Manual mapping: DTO to Entity
        GradingSystem entity = new GradingSystem();
        entity.setVersionName(dto.getVersionName());
        entity.setEffectiveDate(LocalDate.now()); // Auto-fill effective date
        entity.setRemark(dto.getRemark()); // Set remark if provided
        entity.setIntervals(new ArrayList<>());

        // Set department if provided
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            entity.setDepartment(department);
        }

        // Map intervals
        if (dto.getIntervals() != null) {
            for (MarkIntervalDTO intervalDTO : dto.getIntervals()) {
                MarkInterval interval = new MarkInterval();
                interval.setDescription(intervalDTO.getDescription());
                interval.setMin(intervalDTO.getMin());
                interval.setMax(intervalDTO.getMax());
                interval.setGivenValue(intervalDTO.getGivenValue());
                interval.setGradeLetter(intervalDTO.getGradeLetter());
                interval.setGradingSystem(entity);
                entity.getIntervals().add(interval);
            }
        }

        // Validate intervals
        validateIntervals(entity.getIntervals());

        // Save and map back to DTO
        GradingSystem saved = gradingSystemRepository.save(entity);
        return toDTO(saved);
    }

    /**
     * Retrieves a grading system by ID.
     * @param id The grading system ID.
     * @return The GradingSystemDTO.
     * @throws ResourceNotFoundException if not found.
     */
    public GradingSystemDTO getGradingSystem(Long id) {
        GradingSystem entity = gradingSystemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GradingSystem not found with id: " + id));
        return toDTO(entity);
    }

    /**
     * Retrieves all grading systems.
     * @return List of GradingSystemDTOs.
     */
    public List<GradingSystemDTO> getAllGradingSystems() {
        return gradingSystemRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing grading system.
     * @param id The grading system ID.
     * @param dto The updated DTO.
     * @return The updated GradingSystemDTO.
     * @throws ResourceNotFoundException if not found or department not found.
     * @throws IllegalArgumentException if intervals are invalid.
     */
    public GradingSystemDTO updateGradingSystem(Long id, GradingSystemDTO dto) {
        GradingSystem entity = gradingSystemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GradingSystem not found with id: " + id));

        // Update fields
        entity.setVersionName(dto.getVersionName());
        entity.setEffectiveDate(LocalDate.now()); // Auto-update effective date
        entity.setRemark(dto.getRemark()); // Update remark

        // Update department if provided
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            entity.setDepartment(department);
        } else {
            entity.setDepartment(null); // Allow clearing to make global
        }

        // Clear and update intervals
        entity.getIntervals().clear();
        if (dto.getIntervals() != null) {
            for (MarkIntervalDTO intervalDTO : dto.getIntervals()) {
                MarkInterval interval = new MarkInterval();
                interval.setDescription(intervalDTO.getDescription());
                interval.setMin(intervalDTO.getMin());
                interval.setMax(intervalDTO.getMax());
                interval.setGivenValue(intervalDTO.getGivenValue());
                interval.setGradeLetter(intervalDTO.getGradeLetter());
                interval.setGradingSystem(entity);
                entity.getIntervals().add(interval);
            }
        }

        validateIntervals(entity.getIntervals());

        GradingSystem updated = gradingSystemRepository.save(entity);
        return toDTO(updated);
    }

    /**
     * Deletes a grading system by ID.
     * @param id The grading system ID.
     * @throws ResourceNotFoundException if not found.
     */
    public void deleteGradingSystem(Long id) {
        GradingSystem entity = gradingSystemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GradingSystem not found with id: " + id));
        // Add check if in use by batches if needed
        gradingSystemRepository.delete(entity);
    }

    /**
     * Finds the latest grading system for a department.
     * Prefers department-specific; falls back to global (department=null).
     * @param department The department (can be null).
     * @return The GradingSystem.
     * @throws IllegalStateException if no suitable system found.
     */
    public GradingSystem findApplicableGradingSystem(Department department) {
        Pageable topOne = PageRequest.of(0, 1);
        Page<GradingSystem> page = gradingSystemRepository.findLatestByDepartment(department, topOne);
        if (!page.isEmpty()) {
            return page.getContent().get(0);
        }

        // Fallback to global
        page = gradingSystemRepository.findLatestByDepartment(null, topOne);
        if (!page.isEmpty()) {
            return page.getContent().get(0);
        }

        throw new IllegalStateException("No applicable grading system found for department");
    }

    private void validateIntervals(List<MarkInterval> intervals) {
        // Sort by min and check for overlaps
        intervals.sort(Comparator.comparingDouble(MarkInterval::getMin));
        for (int i = 0; i < intervals.size() - 1; i++) {
            MarkInterval current = intervals.get(i);
            MarkInterval next = intervals.get(i + 1);
            if (current.getMax() >= next.getMin()) {
                throw new IllegalArgumentException("Overlapping intervals detected");
            }
            if (current.getMin() >= current.getMax()) {
                throw new IllegalArgumentException("Invalid interval: min must be less than max");
            }
        }
    }

    private GradingSystemDTO toDTO(GradingSystem entity) {
        GradingSystemDTO dto = new GradingSystemDTO();
        dto.setId(entity.getId());
        dto.setVersionName(entity.getVersionName());
        dto.setDepartmentId(entity.getDepartment() != null ? entity.getDepartment().getDptID() : null);
        dto.setRemark(entity.getRemark()); // Include remark in DTO
        List<MarkIntervalDTO> intervalDTOs = entity.getIntervals().stream()
                .map(interval -> new MarkIntervalDTO(
                        interval.getId(),
                        interval.getDescription(),
                        interval.getMin(),
                        interval.getMax(),
                        interval.getGivenValue(),
                        interval.getGradeLetter()
                ))
                .collect(Collectors.toList());
        dto.setIntervals(intervalDTOs);
        return dto;
    }

    // Explanation: Updated service to include remark and retain effectiveDate; fixed findApplicableGradingSystem to use Page.
    // Why: Handles department-specific systems with global fallback; uses latest by ID; includes validation and error handling.
}