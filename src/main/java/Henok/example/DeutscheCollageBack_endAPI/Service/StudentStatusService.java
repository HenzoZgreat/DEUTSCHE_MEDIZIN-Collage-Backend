package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentStatusDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Repository.StudentStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentStatusService {

    @Autowired
    private StudentStatusRepo statusRepository;

    public void addStatuses(List<StudentStatusDTO> statusDTOs) {
        List<StudentStatus> statuses = statusDTOs.stream()
                .map(dto -> new StudentStatus(null, dto.getStatusName()))
                .collect(Collectors.toList());
        statusRepository.saveAll(statuses);
    }

    public List<StudentStatus> getAllStatuses() {
        return statusRepository.findAll();
    }

    public void addStatus(StudentStatusDTO statusDTO) {
        StudentStatus status = new StudentStatus(null, statusDTO.getStatusName());
        statusRepository.save(status);
    }

    public StudentStatus getStatusById(Long id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Status not found: " + id));
    }

    public void updateStatus(Long id, StudentStatusDTO statusDTO) {
        StudentStatus status = getStatusById(id);
        status.setStatusName(statusDTO.getStatusName());
        statusRepository.save(status);
    }

    public void deleteStatus(Long id) {
        statusRepository.deleteById(id);
    }
}