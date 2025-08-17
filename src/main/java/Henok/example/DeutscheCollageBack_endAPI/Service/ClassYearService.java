package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.ClassYearDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.ClassYear;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.ClassYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (classYearRepository.findByClassYear(classYearDTO.getClassYear()).isPresent()) {
            throw new DataIntegrityViolationException("Class year '" + classYearDTO.getClassYear() + "' already exists");
        }
        try {
            ClassYear classYear = new ClassYear();
            classYear.setClassYear(classYearDTO.getClassYear());
            classYearRepository.save(classYear);
            return classYearDTO;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to create class year due to duplicate entry or database constraint");
        }
    }

    public List<ClassYearDTO> getAllClassYears() {
        List<ClassYear> classYears = classYearRepository.findAll();
        if (classYears.isEmpty()) {
            throw new ResourceNotFoundException("No class years found");
        }
        return classYears.stream()
                .map(classYear -> {
                    ClassYearDTO dto = new ClassYearDTO();
                    dto.setClassYear(classYear.getClassYear());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ClassYearDTO getClassYearById(Long id) {
        ClassYear classYear = classYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found with id: " + id));
        ClassYearDTO dto = new ClassYearDTO();
        dto.setClassYear(classYear.getClassYear());
        return dto;
    }

    public ClassYearDTO updateClassYear(Long id, ClassYearDTO classYearDTO) {
        if (classYearDTO.getClassYear() == null || classYearDTO.getClassYear().isEmpty()) {
            throw new IllegalArgumentException("Class year cannot be empty");
        }
        ClassYear existing = classYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found with id: " + id));
        if (!existing.getClassYear().equals(classYearDTO.getClassYear()) &&
                classYearRepository.findByClassYear(classYearDTO.getClassYear()).isPresent()) {
            throw new DataIntegrityViolationException("Class year '" + classYearDTO.getClassYear() + "' already exists");
        }
        try {
            existing.setClassYear(classYearDTO.getClassYear());
            classYearRepository.save(existing);
            return classYearDTO;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to update class year due to duplicate entry or database constraint");
        }
    }

    public void deleteClassYear(Long id) {
        if (!classYearRepository.existsById(id)) {
            throw new ResourceNotFoundException("ClassYear not found with id: " + id);
        }
        try {
            classYearRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Cannot delete class year due to existing dependencies");
        }
    }
}