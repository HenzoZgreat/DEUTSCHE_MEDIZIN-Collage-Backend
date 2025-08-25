package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentStatusDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.StudentStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentStatusService {

    @Autowired
    private StudentStatusRepo studentStatusRepository;

    public void addStudentStatuses(List<StudentStatusDTO> studentStatusDTOs) {
        if (studentStatusDTOs == null || studentStatusDTOs.isEmpty()) {
            throw new IllegalArgumentException("Student status list cannot be null or empty");
        }

        List<StudentStatus> studentStatuses = studentStatusDTOs.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        for (StudentStatus status : studentStatuses) {
            validateStudentStatus(status);
            if (studentStatusRepository.existsByStatusName(status.getStatusName())) {
                throw new IllegalArgumentException("Student status name already exists: " + status.getStatusName());
            }
        }

        studentStatusRepository.saveAll(studentStatuses);
    }

    public void addStudentStatus(StudentStatusDTO studentStatusDTO) {
        if (studentStatusDTO == null) {
            throw new IllegalArgumentException("Student status DTO cannot be null");
        }

        StudentStatus studentStatus = mapToEntity(studentStatusDTO);
        validateStudentStatus(studentStatus);

        if (studentStatusRepository.existsByStatusName(studentStatus.getStatusName())) {
            throw new IllegalArgumentException("Student status name already exists: " + studentStatus.getStatusName());
        }

        studentStatusRepository.save(studentStatus);
    }

    public List<StudentStatus> getAllStudentStatuses() {
        List<StudentStatus> studentStatuses = studentStatusRepository.findAll();
        if (studentStatuses.isEmpty()) {
            throw new ResourceNotFoundException("No student statuses found");
        }
        return studentStatuses;
    }

    public StudentStatus getStudentStatusById(Long id) {
        return studentStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student status not found with id: " + id));
    }

    public void updateStudentStatus(Long id, StudentStatusDTO studentStatusDTO) {
        if (studentStatusDTO == null) {
            throw new IllegalArgumentException("Student status DTO cannot be null");
        }

        StudentStatus existingStatus = getStudentStatusById(id);
        String newStatusName = studentStatusDTO.getStatusName();

        if (!existingStatus.getStatusName().equals(newStatusName) &&
                studentStatusRepository.existsByStatusName(newStatusName)) {
            throw new IllegalArgumentException("Student status name already exists: " + newStatusName);
        }

        existingStatus.setStatusName(newStatusName);
        validateStudentStatus(existingStatus);

        studentStatusRepository.save(existingStatus);
    }

    public void deleteStudentStatus(Long id) {
        if (!studentStatusRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student status not found with id: " + id);
        }
        studentStatusRepository.deleteById(id);
    }

    private StudentStatus mapToEntity(StudentStatusDTO dto) {
        return new StudentStatus(null, dto.getStatusName());
    }

    private void validateStudentStatus(StudentStatus studentStatus) {
        if (studentStatus.getStatusName() == null || studentStatus.getStatusName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student status name cannot be null or empty");
        }
    }
}