package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.ClassYearDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.ClassYear;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.ClassYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassYearService {

    @Autowired
    private ClassYearRepository classYearRepository;

    public ClassYearDTO createClassYear(ClassYearDTO classYearDTO) {
        if (classYearDTO.getClassYear() == null || classYearDTO.getClassYear().isEmpty()) {
            throw new IllegalArgumentException("Class year cannot be empty");
        }
        ClassYear classYear = new ClassYear();
        classYear.setClassYear(classYearDTO.getClassYear());
        classYear = classYearRepository.save(classYear);
        classYearDTO.setId(classYear.getId());
        return classYearDTO;
    }

    public List<ClassYearDTO> getAllClassYears() {
        return classYearRepository.findAll().stream()
                .map(classYear -> {
                    ClassYearDTO dto = new ClassYearDTO();
                    dto.setId(classYear.getId());
                    dto.setClassYear(classYear.getClassYear());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ClassYearDTO getClassYearById(Long id) {
        ClassYear classYear = classYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found with id: " + id));
        ClassYearDTO dto = new ClassYearDTO();
        dto.setId(classYear.getId());
        dto.setClassYear(classYear.getClassYear());
        return dto;
    }

    public ClassYearDTO updateClassYear(Long id, ClassYearDTO classYearDTO) {
        if (classYearDTO.getClassYear() == null || classYearDTO.getClassYear().isEmpty()) {
            throw new IllegalArgumentException("Class year cannot be empty");
        }
        ClassYear classYear = classYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found with id: " + id));
        classYear.setClassYear(classYearDTO.getClassYear());
        classYearRepository.save(classYear);
        classYearDTO.setId(id);
        return classYearDTO;
    }

    public void deleteClassYear(Long id) {
        if (!classYearRepository.existsById(id)) {
            throw new ResourceNotFoundException("ClassYear not found with id: " + id);
        }
        classYearRepository.deleteById(id);
    }
}
