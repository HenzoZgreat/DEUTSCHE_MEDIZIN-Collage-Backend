package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.DTO.DepartmentDTO;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentRepo;
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
    private NotificationService notificationService;

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

    public List<Department> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        if (departments.isEmpty()) {
            throw new ResourceNotFoundException("No departments found");
        }
        return departments;
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    public void updateDepartment(Long id, DepartmentDTO departmentDTO) {
        if (departmentDTO == null) {
            throw new IllegalArgumentException("Department DTO cannot be null");
        }

        Department existingDepartment = getDepartmentById(id);
        String newDeptCode = departmentDTO.getDepartmentCode();

        if (!existingDepartment.getDepartmentCode().equals(newDeptCode) &&
                departmentRepository.existsByDepartmentCode(newDeptCode)) {
            throw new IllegalArgumentException("Department code already exists: " + newDeptCode);
        }

        existingDepartment.setDeptName(departmentDTO.getDeptName());
        existingDepartment.setTotalCrHr(departmentDTO.getTotalCrHr());
        existingDepartment.setDepartmentCode(newDeptCode);
        validateDepartment(existingDepartment);

        departmentRepository.save(existingDepartment);
    }

    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }

    private Department mapToEntity(DepartmentDTO dto) {
        return new Department(null, dto.getDeptName(), dto.getTotalCrHr(), dto.getDepartmentCode());
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
