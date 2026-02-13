package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.ProgressionSequenceDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.ClassYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.ProgressionSequence;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.ClassYearRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.SemesterRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.ProgressionSequenceRepository;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
public class ProgressionSequenceService {
    private final ProgressionSequenceRepository progressionSequenceRepository;
    private final DepartmentRepo departmentRepository;
    private final ClassYearRepository classYearRepository;
    private final SemesterRepo semesterRepository;

    @Transactional
    public Map<String, Object> createBulk(List<Map<String, Object>> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new BadRequestException("Bulk create request cannot be empty");
        }

        int totalRequested = requests.size();
        int successCount = 0;
        List<Map<String, Object>> errorResults = new ArrayList<>();

        for (Map<String, Object> req : requests) {
            try {
                // Extract required fields (you can add more validation if needed)
                Long deptId = req.get("departmentId") != null
                        ? ((Number) req.get("departmentId")).longValue()
                        : null;
                Long classYearId = ((Number) req.get("classYearId")).longValue();
                String semesterId = (String) req.get("semesterId");
                Integer seqNumber = ((Number) req.get("sequenceNumber")).intValue();
                String description = (String) req.get("description");

                if (classYearId == null || semesterId == null || semesterId.isBlank() || seqNumber == null || seqNumber <= 0) {
                    throw new BadRequestException("Missing or invalid required fields");
                }

                // Build lookup keys
                ClassYear cy = classYearRepository.findById(classYearId)
                        .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found"));
                Semester sem = semesterRepository.findById(semesterId)
                        .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));

                Department dept = null;
                if (deptId != null) {
                    dept = departmentRepository.findById(deptId)
                            .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
                }

                // Check if already exists (specific or global)
                Optional<ProgressionSequence> existing;
                if (dept != null) {
                    existing = progressionSequenceRepository.findByDepartmentAndClassYearAndSemester(dept, cy, sem);
                } else {
                    existing = progressionSequenceRepository.findByDepartmentIsNullAndClassYearAndSemester(cy, sem);
                }

                if (existing.isPresent()) {
                    // Skip duplicate - treat as "already exists" (not an error, but not success)
                    continue;
                }

                // Create new
                ProgressionSequence entity = ProgressionSequence.builder()
                        .department(dept)
                        .classYear(cy)
                        .semester(sem)
                        .sequenceNumber(seqNumber)
                        .description(description)
                        .build();

                progressionSequenceRepository.save(entity);
                successCount++;

            } catch (Exception ex) {
                // Collect error info
                Map<String, Object> errorEntry = new HashMap<>();
                String inputDesc = String.format("%s-%s",
                        req.get("classYearId") != null ? req.get("classYearId") : "unknown",
                        req.get("semesterId") != null ? req.get("semesterId") : "unknown");

                errorEntry.put("input", inputDesc);
                errorEntry.put("reason", ex.getMessage() != null ? ex.getMessage() : "Unknown error");

                errorResults.add(errorEntry);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalRequested", totalRequested);
        response.put("totalFailed", errorResults.size());
        response.put("results", errorResults);  // only errors here

        return response;
    }

    public List<ProgressionSequenceDTO> getAll(@Nullable Long departmentId, boolean sortBySequence) {
        List<ProgressionSequence> sequences;

        if (departmentId != null) {
            Department dept = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + departmentId));
            sequences = progressionSequenceRepository.findByDepartment(dept);
        } else {
            sequences = progressionSequenceRepository.findAll();
        }

        if (sortBySequence) {
            sequences = sequences.stream()
                    .sorted(Comparator.comparing(ProgressionSequence::getSequenceNumber))
                    .toList();
        }

        return sequences.stream().map(this::mapToDTO).toList();
    }

    @Transactional
    public ProgressionSequenceDTO updateOne(Long id, ProgressionSequenceDTO dto) {
        ProgressionSequence entity = progressionSequenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Progression sequence not found: " + id));

        validate(dto, entity);
        mapToEntity(dto, entity);

        return mapToDTO(progressionSequenceRepository.save(entity));
    }

    @Transactional
    public List<ProgressionSequenceDTO> updateBulk(List<ProgressionSequenceDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new BadRequestException("Bulk update request cannot be empty");
        }

        List<ProgressionSequence> updated = new ArrayList<>();

        for (ProgressionSequenceDTO dto : dtos) {
            if (dto.getId() == null) {
                throw new BadRequestException("ID is required for update");
            }
            ProgressionSequence entity = progressionSequenceRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found: " + dto.getId()));

            validate(dto, entity);
            mapToEntity(dto, entity);

            updated.add(progressionSequenceRepository.save(entity));
        }

        return updated.stream().map(this::mapToDTO).toList();
    }

    @Transactional
    public void deleteBulk(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("Bulk delete request cannot be empty");
        }

        List<ProgressionSequence> entities = progressionSequenceRepository.findAllById(ids);
        if (entities.size() != ids.size()) {
            throw new ResourceNotFoundException("Some IDs not found in delete request");
        }

        progressionSequenceRepository.deleteAll(entities);
    }

    // ── Validation ───────────────────────────────────────────────────────────────
    private void validate(ProgressionSequenceDTO dto, @Nullable ProgressionSequence existing) {
        if (dto.getClassYearId() == null) {
            throw new BadRequestException("ClassYear ID is required");
        }
        if (dto.getSemesterId() == null || dto.getSemesterId().isBlank()) {
            throw new BadRequestException("Semester code is required");
        }
        if (dto.getSequenceNumber() == null || dto.getSequenceNumber() <= 0) {
            throw new BadRequestException("Sequence number must be positive integer");
        }

        ClassYear cy = classYearRepository.findById(dto.getClassYearId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found with Id : " + dto.getClassYearId()));

        Semester sem = semesterRepository.findById(dto.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with Code : " + dto.getSemesterId()));

        // Check for conflict (global or specific)
        Optional<ProgressionSequence> conflict;
        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id : " + dto.getDepartmentId()));
            conflict = progressionSequenceRepository.findByDepartmentAndClassYearAndSemester(dept, cy, sem);
        } else {
            conflict = progressionSequenceRepository.findByDepartmentIsNullAndClassYearAndSemester(cy, sem);
        }

        if (conflict.isPresent() && (existing == null || !conflict.get().getId().equals(existing.getId()))) {
            throw new BadRequestException("Progression sequence already exists for this combination");
        }
    }

    // ── Mapping ──────────────────────────────────────────────────────────────────
    private ProgressionSequence mapToEntity(ProgressionSequenceDTO dto, @Nullable ProgressionSequence target) {
        ProgressionSequence e = target != null ? target : new ProgressionSequence();

        e.setClassYear(classYearRepository.getReferenceById(dto.getClassYearId()));
        e.setSemester(semesterRepository.getReferenceById(dto.getSemesterId()));
        e.setSequenceNumber(dto.getSequenceNumber());
        e.setDescription(dto.getDescription());

        if (dto.getDepartmentId() != null) {
            e.setDepartment(departmentRepository.getReferenceById(dto.getDepartmentId()));
        } else {
            e.setDepartment(null);  // global
        }

        return e;
    }

    private ProgressionSequenceDTO mapToDTO(ProgressionSequence e) {
        return ProgressionSequenceDTO.builder()
                .id(e.getId())
                .departmentId(e.getDepartment() != null ? e.getDepartment().getDptID() : null)
                .departmentName(e.getDepartment() != null ? e.getDepartment().getDeptName() : null)
                .departmentCode(e.getDepartment() != null ? e.getDepartment().getDepartmentCode() : null)
                .classYearId(e.getClassYear().getId())
                .classYearName(e.getClassYear().getClassYear())
                .semesterId(e.getSemester().getAcademicPeriodCode())
                .semesterName(e.getSemester().getAcademicPeriod())
                .sequenceNumber(e.getSequenceNumber())
                .description(e.getDescription())
                .build();
    }
}
