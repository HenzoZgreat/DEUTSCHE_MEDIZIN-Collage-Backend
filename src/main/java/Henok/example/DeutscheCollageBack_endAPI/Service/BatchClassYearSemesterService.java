package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.BatchClassYearSemesterDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Batch;
import Henok.example.DeutscheCollageBack_endAPI.Entity.BatchClassYearSemester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.ClassYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.GradingSystem;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.BatchClassYearSemesterRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.BatchRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.ClassYearRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.GradingSystemRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.AcademicYearRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.SemesterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchClassYearSemesterService {

    @Autowired
    private BatchClassYearSemesterRepo batchClassYearSemesterRepository;

    @Autowired
    private BatchRepo batchRepository;

    @Autowired
    private ClassYearRepository classYearRepository;

    @Autowired
    private SemesterRepo semesterRepository;

    @Autowired
    private AcademicYearRepo academicYearRepository;

    @Autowired
    private GradingSystemRepository gradingSystemRepository;

    /**
     * Saves a list of batch-class-year-semester combinations.
     * @param combinations The list of combinations to save.
     */
    public void saveAll(List<BatchClassYearSemester> combinations) {
        batchClassYearSemesterRepository.saveAll(combinations);
    }

    /**
     * Creates a new batch-class-year-semester combination.
     * @param dto The input DTO.
     * @return The created BatchClassYearSemesterDTO.
     * @throws ResourceNotFoundException if referenced entities not found.
     * @throws IllegalArgumentException if data is invalid.
     */
    public BatchClassYearSemesterDTO createBatchClassYearSemester(BatchClassYearSemesterDTO dto) {
        Batch batch = batchRepository.findById(dto.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + dto.getBatchId()));

        ClassYear classYear = classYearRepository.findById(dto.getClassYearId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found with id: " + dto.getClassYearId()));

        Semester semester = semesterRepository.findById(dto.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id: " + dto.getSemesterId()));

        AcademicYear entryYear = null;
        if (dto.getEntryYearId() != null) {
            entryYear = academicYearRepository.findById(dto.getEntryYearId())
                    .orElseThrow(() -> new ResourceNotFoundException("AcademicYear not found with id: " + dto.getEntryYearId()));
        }

        Pageable topOne = PageRequest.of(0, 1);
        Page<GradingSystem> page = gradingSystemRepository.findLatestByEffectiveDate(topOne);
        GradingSystem gradingSystem = page.getContent().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No grading system found"));

        BatchClassYearSemester entity = new BatchClassYearSemester();
        entity.setBatch(batch);
        entity.setClassYear(classYear);
        entity.setSemester(semester);
        entity.setEntryYear(entryYear);
        entity.setClassStart_GC(dto.getClassStartGC());
        entity.setClassStart_EC(dto.getClassStartEC());
        entity.setClassEnd_GC(dto.getClassEndGC());
        entity.setClassEnd_EC(dto.getClassEndEC());
        entity.setGradingSystem(gradingSystem);

        validateEntity(entity);

        BatchClassYearSemester saved = batchClassYearSemesterRepository.save(entity);
        return toDTO(saved);
    }

    /**
     * Retrieves a batch-class-year-semester by ID.
     * @param id The batch-class-year-semester ID.
     * @return The BatchClassYearSemesterDTO.
     * @throws ResourceNotFoundException if not found.
     */
    public BatchClassYearSemesterDTO getBatchClassYearSemester(Long id) {
        BatchClassYearSemester entity = batchClassYearSemesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + id));
        return toDTO(entity);
    }

    /**
     * Retrieves all batch-class-year-semester combinations.
     * @return List of BatchClassYearSemesterDTOs.
     */
    public List<BatchClassYearSemesterDTO> getAllBatchClassYearSemesters() {
        return batchClassYearSemesterRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates a batch-class-year-semester combination.
     * @param id The batch-class-year-semester ID.
     * @param dto The updated data.
     * @return The updated BatchClassYearSemesterDTO.
     * @throws ResourceNotFoundException if not found.
     * @throws IllegalArgumentException if data is invalid.
     */
    public BatchClassYearSemesterDTO updateBatchClassYearSemester(Long id, BatchClassYearSemesterDTO dto) {
        BatchClassYearSemester entity = batchClassYearSemesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + id));

        Batch batch = batchRepository.findById(dto.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + dto.getBatchId()));

        ClassYear classYear = classYearRepository.findById(dto.getClassYearId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found with id: " + dto.getClassYearId()));

        Semester semester = semesterRepository.findById(dto.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id: " + dto.getSemesterId()));

        AcademicYear entryYear = null;
        if (dto.getEntryYearId() != null) {
            entryYear = academicYearRepository.findById(dto.getEntryYearId())
                    .orElseThrow(() -> new ResourceNotFoundException("AcademicYear not found with id: " + dto.getEntryYearId()));
        }

        Pageable topOne = PageRequest.of(0, 1);
        Page<GradingSystem> page = gradingSystemRepository.findLatestByEffectiveDate(topOne);
        GradingSystem gradingSystem = page.getContent().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No grading system found"));

        entity.setBatch(batch);
        entity.setClassYear(classYear);
        entity.setSemester(semester);
        entity.setEntryYear(entryYear);
        entity.setClassStart_GC(dto.getClassStartGC());
        entity.setClassStart_EC(dto.getClassStartEC());
        entity.setClassEnd_GC(dto.getClassEndGC());
        entity.setClassEnd_EC(dto.getClassEndEC());
        entity.setGradingSystem(gradingSystem);

        validateEntity(entity);

        BatchClassYearSemester updated = batchClassYearSemesterRepository.save(entity);
        return toDTO(updated);
    }

    /**
     * Assigns a grading system to a batch-class-year-semester.
     * @param id The batch-class-year-semester ID.
     * @param gradingSystemId The grading system ID.
     * @throws ResourceNotFoundException if not found.
     */
    public void assignGradingSystem(Long id, Long gradingSystemId) {
        BatchClassYearSemester entity = batchClassYearSemesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + id));

        GradingSystem gs = gradingSystemRepository.findById(gradingSystemId)
                .orElseThrow(() -> new ResourceNotFoundException("GradingSystem not found with id: " + gradingSystemId));

        entity.setGradingSystem(gs);
        batchClassYearSemesterRepository.save(entity);
    }

    /**
     * Deletes a batch-class-year-semester combination.
     * @param id The batch-class-year-semester ID.
     * @throws ResourceNotFoundException if not found.
     */
    public void deleteBatchClassYearSemester(Long id) {
        BatchClassYearSemester entity = batchClassYearSemesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + id));
        batchClassYearSemesterRepository.delete(entity);
    }

    /**
     * Validates the batch-class-year-semester entity.
     * @param entity The entity to validate.
     * @throws IllegalArgumentException if data is invalid.
     */
    private void validateEntity(BatchClassYearSemester entity) {
        if (entity.getBatch() == null || entity.getClassYear() == null || entity.getSemester() == null || entity.getGradingSystem() == null) {
            throw new IllegalArgumentException("Batch, ClassYear, Semester, and GradingSystem must not be null");
        }
        // Add more validation if needed (e.g., date consistency)
    }

    /**
     * Converts BatchClassYearSemester entity to DTO.
     * @param entity The BatchClassYearSemester entity.
     * @return The BatchClassYearSemesterDTO.
     */
    private BatchClassYearSemesterDTO toDTO(BatchClassYearSemester entity) {
        BatchClassYearSemesterDTO dto = new BatchClassYearSemesterDTO();
        dto.setBcysId(entity.getBcysID());
        dto.setBatchId(entity.getBatch().getId());
        dto.setClassYearId(entity.getClassYear().getId());
        dto.setSemesterId(entity.getSemester().getAcademicPeriodCode());
        dto.setEntryYearId(entity.getEntryYear() != null ? entity.getEntryYear().getYearCode() : null);
        dto.setClassStartGC(entity.getClassStart_GC());
        dto.setClassStartEC(entity.getClassStart_EC());
        dto.setClassEndGC(entity.getClassEnd_GC());
        dto.setClassEndEC(entity.getClassEnd_EC());
        dto.setGradingSystemId(entity.getGradingSystem().getId());
        // Construct name: batchName-classYear-semesterCode
        String name = String.format("%s-%s-%s",
                entity.getBatch().getBatchName(),
                entity.getClassYear().getClassYear(),
                entity.getSemester().getAcademicPeriodCode());
        dto.setName(name);
        return dto;
    }

    // Explanation: Updated to include name field in DTO for GET requests.
    // Why: Constructs name as batchName-classYear-semesterCode; supports all CRUD operations.
}