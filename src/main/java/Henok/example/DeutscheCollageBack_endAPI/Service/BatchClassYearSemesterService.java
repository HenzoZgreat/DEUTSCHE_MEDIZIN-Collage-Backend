package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.BatchClassYearSemesterDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.AcademicYearRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.SemesterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BatchClassYearSemesterService {

    @Autowired
    private BatchClassYearSemesterRepo bcysRepo;

    @Autowired
    private BatchRepo batchRepo;

    @Autowired
    private ClassYearRepository classYearRepo;

    @Autowired
    private SemesterRepo semesterRepo;

    @Autowired
    private AcademicYearRepo academicYearRepo;

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private DepartmentBCYSRepository departmentBCYSRepo;



    /**
     * Saves a list of batch-class-year-semester combinations.
     * @param combinations The list of combinations to save.
     */
    public void saveAll(List<BatchClassYearSemester> combinations) {
        bcysRepo.saveAll(combinations);
    }

    /**
     * Creates a new BatchClassYearSemester (core combination) and links it to one or more departments
     * with their specific academic year and class start/end dates.
     */
    @Transactional
    public BatchClassYearSemesterDTO createBatchClassYearSemester(BatchClassYearSemesterDTO dto) {
        if (dto.getDepartmentUpdates() == null || dto.getDepartmentUpdates().isEmpty()) {
            throw new IllegalArgumentException("At least one department must be provided with academic year and dates");
        }

        // Validate and load core entities
        Batch batch = batchRepo.findById(dto.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + dto.getBatchId()));

        ClassYear classYear = classYearRepo.findById(dto.getClassYearId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found with id: " + dto.getClassYearId()));

        Semester semester = semesterRepo.findById(dto.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id: " + dto.getSemesterId()));

        // Prevent duplicate BCYS (batch + classYear + semester)
        boolean exists = bcysRepo.existsByBatchAndClassYearAndSemester(batch, classYear, semester);
        if (exists) {
            throw new IllegalArgumentException(
                    "BatchClassYearSemester already exists for batch: " + batch.getBatchName() +
                            ", classYear: " + classYear.getClassYear() +
                            ", semester: " + semester.getAcademicPeriodCode()
            );
        }

        // Create core BCYS
        BatchClassYearSemester bcys = new BatchClassYearSemester();
        bcys.setBatch(batch);
        bcys.setClassYear(classYear);
        bcys.setSemester(semester);
        bcys = bcysRepo.save(bcys);

        // Create department-specific links
        List<DepartmentBCYS> links = new ArrayList<>();

        for (BatchClassYearSemesterDTO.DepartmentUpdateItem item : dto.getDepartmentUpdates()) {
            if (item.getDepartmentId() == null) {
                throw new IllegalArgumentException("departmentId is required");
            }

            Department dept = departmentRepo.findById(item.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + item.getDepartmentId()));

            AcademicYear year = null;
            if (item.getEntryYearId() != null) {
                year = academicYearRepo.findById(item.getEntryYearId())
                        .orElseThrow(() -> new ResourceNotFoundException("AcademicYear not found: " + item.getEntryYearId()));
            }

            DepartmentBCYS link = DepartmentBCYS.builder()
                    .bcys(bcys)
                    .department(dept)
                    .academicYear(year)
                    .classStartGC(item.getClassStartGC())
                    .classStartEC(item.getClassStartEC())
                    .classEndGC(item.getClassEndGC())
                    .classEndEC(item.getClassEndEC())
                    .build();

            links.add(link);
        }

        departmentBCYSRepo.saveAll(links);

        return toDTO(bcys, links);
    }

    /**
     * Retrieves a single BatchClassYearSemester with all its department-specific details.
     */
    public BatchClassYearSemesterDTO getBatchClassYearSemester(Long id) {
        BatchClassYearSemester bcys = bcysRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + id));

        List<DepartmentBCYS> links = departmentBCYSRepo.findByBcys(bcys);
        return toDTO(bcys, links);
    }

    /**
     * Returns all BatchClassYearSemester entries, each enriched with their department details.
     */
    public List<BatchClassYearSemesterDTO> getAllBatchClassYearSemesters() {
        return bcysRepo.findAll().stream()
                .map(bcys -> {
                    List<DepartmentBCYS> links = departmentBCYSRepo.findByBcys(bcys);
                    return toDTO(bcys, links);
                })
                .collect(Collectors.toList());
    }

    /**
     * Updates the BatchClassYearSemester core fields and/or department-specific metadata.
     * Supports:
     * - Changing batch, class year, semester
     * - Adding new departments
     * - Removing departments
     * - Updating academic year / dates for specific departments
     */
    @Transactional
    public BatchClassYearSemesterDTO updateBatchClassYearSemester(Long id, BatchClassYearSemesterDTO dto) {
        BatchClassYearSemester bcys = bcysRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + id));

        // ── Update core fields (batch, class year, semester) ────────────────────────────────
        boolean coreChanged = false;

        if (dto.getBatchId() != null) {
            Batch batch = batchRepo.findById(dto.getBatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
            bcys.setBatch(batch);
            coreChanged = true;
        }

        if (dto.getClassYearId() != null) {
            ClassYear cy = classYearRepo.findById(dto.getClassYearId())
                    .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found"));
            bcys.setClassYear(cy);
            coreChanged = true;
        }

        if (dto.getSemesterId() != null) {
            Semester sem = semesterRepo.findById(dto.getSemesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
            bcys.setSemester(sem);
            coreChanged = true;
        }

        if (coreChanged) {
            bcys = bcysRepo.save(bcys);
        }

        // Re-check uniqueness only if batch, classYear, or semester was changed
        if (coreChanged) {
            boolean exists = bcysRepo.existsByBatchAndClassYearAndSemesterAndBcysIDNot(
                    bcys.getBatch(), bcys.getClassYear(), bcys.getSemester(), bcys.getBcysID()
            );
            if (exists) {
                throw new IllegalArgumentException(
                        "Another BatchClassYearSemester already exists for batch: " + bcys.getBatch().getBatchName() +
                                ", classYear: " + bcys.getClassYear().getClassYear() +
                                ", semester: " + bcys.getSemester().getAcademicPeriodCode()
                );
            }
        }

        // ── Handle department updates / add / remove ───────────────────────────────────────
        // Load current links into a map for fast lookup
        List<DepartmentBCYS> currentLinks = departmentBCYSRepo.findByBcys(bcys);
        Map<Long, DepartmentBCYS> linkMap = currentLinks.stream()
                .collect(Collectors.toMap(
                        l -> l.getDepartment().getDptID(),
                        l -> l
                ));

        // Process each update item
        for (BatchClassYearSemesterDTO.DepartmentUpdateItem item : dto.getDepartmentUpdates()) {
            Long deptId = item.getDepartmentId();
            if (deptId == null) {
                throw new IllegalArgumentException("departmentId is required in update item");
            }

            Department dept = departmentRepo.findById(deptId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + deptId));

            if (item.isRemove()) {
                // Remove department link if exists
                DepartmentBCYS existing = linkMap.get(deptId);
                if (existing != null) {
                    departmentBCYSRepo.delete(existing);
                    linkMap.remove(deptId);
                }
                continue;
            }

            // Add or update
            DepartmentBCYS link = linkMap.get(deptId);
            boolean isNew = (link == null);

            if (isNew) {
                link = new DepartmentBCYS();
                link.setBcys(bcys);
                link.setDepartment(dept);
            }

            // Update only provided fields (null = no change)
            if (item.getEntryYearId() != null) {
                AcademicYear year = academicYearRepo.findById(item.getEntryYearId())
                        .orElseThrow(() -> new ResourceNotFoundException("AcademicYear not found"));
                link.setAcademicYear(year);
            }

            if (item.getClassStartGC() != null) link.setClassStartGC(item.getClassStartGC());
            if (item.getClassStartEC() != null) link.setClassStartEC(item.getClassStartEC());
            if (item.getClassEndGC() != null)   link.setClassEndGC(item.getClassEndGC());
            if (item.getClassEndEC() != null)   link.setClassEndEC(item.getClassEndEC());

            if (isNew) {
                departmentBCYSRepo.save(link);
                linkMap.put(deptId, link);
            } else {
                departmentBCYSRepo.save(link);
            }
        }

        // Return updated view
        List<DepartmentBCYS> finalLinks = departmentBCYSRepo.findByBcys(bcys);
        return toDTO(bcys, finalLinks);
    }

    /**
     * Deletes the BatchClassYearSemester and all its department links.
     */
    @Transactional
    public Map<String, Object> deleteBatchClassYearSemester(Long id) {
        BatchClassYearSemester bcys = bcysRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + id));

        String displayName = bcys.getDisplayName();

        departmentBCYSRepo.deleteByBcys(bcys);
        bcysRepo.delete(bcys);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "BatchClassYearSemester deleted successfully");
        response.put("deletedDisplayName", displayName);
        return response;
    }

    // ────────────────────────────────────────────────────────────────────────────────
    // Conversion to DTO (shows core + per-department details)
    // ────────────────────────────────────────────────────────────────────────────────
    private BatchClassYearSemesterDTO toDTO(BatchClassYearSemester bcys, List<DepartmentBCYS> links) {
        BatchClassYearSemesterDTO dto = new BatchClassYearSemesterDTO();

        dto.setBcysId(bcys.getBcysID());
        dto.setBatchId(bcys.getBatch().getId());
        dto.setClassYearId(bcys.getClassYear().getId());
        dto.setSemesterId(bcys.getSemester().getAcademicPeriodCode());
        dto.setName(bcys.getDisplayName());

        List<BatchClassYearSemesterDTO.DepartmentCohortInfo> deptInfos = links.stream()
                .map(link -> {
                    BatchClassYearSemesterDTO.DepartmentCohortInfo info = new BatchClassYearSemesterDTO.DepartmentCohortInfo();
                    info.setDepartmentId(link.getDepartment().getDptID());
                    info.setDepartmentName(link.getDepartment().getDeptName());
                    info.setDepartmentCode(link.getDepartment().getDepartmentCode());

                    if (link.getAcademicYear() != null) {
                        info.setEntryYearId(link.getAcademicYear().getYearCode());
                        info.setAcademicYearGC(link.getAcademicYear() != null ? link.getAcademicYear().getAcademicYearGC() : null);
                        info.setAcademicYearEC(link.getAcademicYear() != null ? link.getAcademicYear().getAcademicYearEC() : null);
                    }
                    info.setClassStartGC(link.getClassStartGC());
                    info.setClassStartEC(link.getClassStartEC());
                    info.setClassEndGC(link.getClassEndGC());
                    info.setClassEndEC(link.getClassEndEC());

                    return info;
                })
                .collect(Collectors.toList());

        dto.setDepartments(deptInfos);

        return dto;
    }
}