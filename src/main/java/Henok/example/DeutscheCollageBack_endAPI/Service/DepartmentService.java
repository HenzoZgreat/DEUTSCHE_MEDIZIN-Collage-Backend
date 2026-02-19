package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.DepartmentResponseDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.DTO.DepartmentDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramLevel;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramModality;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ProgramLevelRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ProgramModalityRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.StudentDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepo departmentRepository;

    @Autowired
    private ProgramModalityRepository programModalityRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProgramLevelRepository programLevelRepository;

    @Autowired
    private StudentDetailsRepository studentDetailsRepository;

    public void addDepartments(List<DepartmentDTO> departmentDTOs) {
        if (departmentDTOs == null || departmentDTOs.isEmpty()) {
            throw new IllegalArgumentException("Department list cannot be null or empty");
        }

        List<Department> departments = departmentDTOs.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        for (Department department : departments) {
            validateDepartment(department);
            if (departmentRepository.existsByDepartmentCode(department.getDepartmentCode())) {
                throw new IllegalArgumentException("Department code already exists: " + department.getDepartmentCode());
            }
        }

        departmentRepository.saveAll(departments);
    }

    public void addDepartment(DepartmentDTO departmentDTO) {
        if (departmentDTO == null) {
            throw new IllegalArgumentException("Department DTO cannot be null");
        }

        Department department = mapToEntity(departmentDTO);
        validateDepartment(department);

        if (departmentRepository.existsByDepartmentCode(department.getDepartmentCode())) {
            throw new IllegalArgumentException("Department code already exists: " + department.getDepartmentCode());
        }

        departmentRepository.save(department);
        notificationService.createNotification(Arrays.asList(
                        Role.GENERAL_MANAGER, Role.DEAN, Role.VICE_DEAN, Role.DEPARTMENT_HEAD, Role.FINANCIAL_STAFF),
                null, Role.REGISTRAR,
                "Added a new Department : " + department.getDeptName());
    }


    private DepartmentResponseDTO toResponseDTO(Department department) {
        DepartmentResponseDTO.ModalityDTO modalityDTO = null;
        if (department.getProgramModality() != null) {
            DepartmentResponseDTO.LevelDTO levelDTO = null;
            if (department.getProgramModality().getProgramLevel() != null) {
                ProgramLevel level = department.getProgramModality().getProgramLevel();
                levelDTO = new DepartmentResponseDTO.LevelDTO(
                        level.getCode(),
                        level.getName(),
                        level.getActive()
                );
            }
            modalityDTO = new DepartmentResponseDTO.ModalityDTO(
                    department.getProgramModality().getModalityCode(),
                    department.getProgramModality().getModality(),
                    levelDTO
            );
        }

        DepartmentResponseDTO.LevelDTO directLevelDTO = null;
        if (department.getProgramLevel() != null) {
            ProgramLevel directLevel = department.getProgramLevel();
            directLevelDTO = new DepartmentResponseDTO.LevelDTO(
                    directLevel.getCode(),
                    directLevel.getName(),
                    directLevel.getActive()
            );
        }

        return new DepartmentResponseDTO(
                department.getDptID(),
                department.getDeptName(),
                department.getTotalCrHr(),
                department.getDepartmentCode(),
                modalityDTO,
                directLevelDTO
        );
    }

    public List<DepartmentResponseDTO> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        if (departments.isEmpty()) {
            throw new ResourceNotFoundException("No departments found");
        }
        return departments.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return toResponseDTO(department);
    }

    public Department getDepartmentByUsingId(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return department;
    }

    public List<Department> getDepartmentsByModality(String modalityCode) {
        ProgramModality modality = programModalityRepository.findById(modalityCode)
                .orElseThrow(() -> new ResourceNotFoundException("Program modality not found with code: " + modalityCode));

        List<Department> departments = departmentRepository.findByProgramModality(modality);
        if (departments.isEmpty()) {
            throw new ResourceNotFoundException("No departments found for program modality: " + modalityCode);
        }
        return departments;
    }

    public List<Department> getDepartmentsByLevel(String levelCode) {
        ProgramLevel level = programLevelRepository.findById(levelCode)
                .orElseThrow(() -> new ResourceNotFoundException("Program level not found with code: " + levelCode));

        List<Department> departments = departmentRepository.findByProgramLevel(level);
        if (departments.isEmpty()) {
            throw new ResourceNotFoundException("No departments found for program level: " + levelCode);
        }
        return departments;
    }

    public void updateDepartment(Long id, DepartmentDTO departmentDTO) {
        if (departmentDTO == null) {
            throw new IllegalArgumentException("Department DTO cannot be null");
        }

        Department existingDepartment = getDepartmentByUsingId(id);
        String newDeptCode = departmentDTO.getDepartmentCode();

        if (newDeptCode != null && !existingDepartment.getDepartmentCode().equals(newDeptCode) &&
                departmentRepository.existsByDepartmentCode(newDeptCode)) {
            throw new IllegalArgumentException("Department code already exists: " + newDeptCode);
        }

        if (departmentDTO.getDeptName() != null) {
            existingDepartment.setDeptName(departmentDTO.getDeptName());
        }
        if (departmentDTO.getTotalCrHr() != null) {
            existingDepartment.setTotalCrHr(departmentDTO.getTotalCrHr());
        }
        if (newDeptCode != null) {
            existingDepartment.setDepartmentCode(newDeptCode);
        }
        if (departmentDTO.getModalityCode() != null) {
            ProgramModality programModality = programModalityRepository.findByModalityCode(departmentDTO.getModalityCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Program modality not found with code: " + departmentDTO.getModalityCode()));
            existingDepartment.setProgramModality(programModality);
        } else {
            existingDepartment.setProgramModality(null);
        }
        if (departmentDTO.getProgramLevelCode() != null) {
            ProgramLevel programLevel = programLevelRepository.findById(departmentDTO.getProgramLevelCode())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Program level not found with code: " + departmentDTO.getProgramLevelCode()));
            existingDepartment.setProgramLevel(programLevel);
        } else {
            existingDepartment.setProgramLevel(null);
        }

        validateDepartment(existingDepartment);
        departmentRepository.save(existingDepartment);
    }

    // Service method to delete a department
    // Checks if the department exists and if it is referenced by any students before deletion.
    // Why: Prevents deletion of departments that are in use to maintain data integrity.
    // If referenced, throws BadRequestException to indicate the operation is invalid.
    public void deleteDepartment(Long id) {
        // Check if the department exists
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        // Check if the department is referenced by any students
        // Property: Uses countByDepartmentEnrolled to efficiently check references without loading all entities.
        long studentCount = studentDetailsRepository.countByDepartmentEnrolled(department);
        if (studentCount > 0) {
            throw new BadRequestException("Cannot delete department as it is referenced by " + studentCount + " students.");
        }

        // TODO: Add checks for other potential references (e.g., courses, teachers) if applicable.
        // For now, assuming primary reference is via students based on provided entities.

        // Proceed with deletion if no references found
        departmentRepository.deleteById(id);
    }

    private Department mapToEntity(DepartmentDTO dto) {
        ProgramLevel programLevel = null;
        if (dto.getProgramLevelCode() != null) {
            programLevel = programLevelRepository.findById(dto.getProgramLevelCode())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Program level not found with code: " + dto.getProgramLevelCode()));
        }
        return new Department(null, dto.getDeptName(), dto.getTotalCrHr(),
                dto.getDepartmentCode(), null, programLevel);   // null = programModality (keep existing)
    }

    private void validateDepartment(Department department) {
        if (department.getDeptName() == null || department.getDeptName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        if (department.getDepartmentCode() == null || department.getDepartmentCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Department code cannot be null or empty");
        }
    }
}
