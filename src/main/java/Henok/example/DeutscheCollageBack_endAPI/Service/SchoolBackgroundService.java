package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.SchoolBackgroundDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.SchoolBackground;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.SchoolBackgroundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchoolBackgroundService {

    @Autowired
    private SchoolBackgroundRepository schoolBackgroundRepository;

    public SchoolBackgroundDTO createSchoolBackground(SchoolBackgroundDTO schoolBackgroundDTO) {
        // Validate input
        if (schoolBackgroundDTO.getBackground() == null || schoolBackgroundDTO.getBackground().isEmpty()) {
            throw new IllegalArgumentException("Background cannot be empty");
        }
        // Check for duplicate
        if (schoolBackgroundRepository.findByBackground(schoolBackgroundDTO.getBackground()).isPresent()) {
            throw new DataIntegrityViolationException("Background '" + schoolBackgroundDTO.getBackground() + "' already exists");
        }
        try {
            SchoolBackground schoolBackground = new SchoolBackground();
            schoolBackground.setBackground(schoolBackgroundDTO.getBackground());
            schoolBackgroundRepository.save(schoolBackground);
            return schoolBackgroundDTO;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to create school background due to database constraint");
        }
    }

    public List<SchoolBackgroundDTO> getAllSchoolBackgrounds() {
        List<SchoolBackground> schoolBackgrounds = schoolBackgroundRepository.findAll();
        // Check if empty
        if (schoolBackgrounds.isEmpty()) {
            throw new ResourceNotFoundException("No school backgrounds found");
        }
        return schoolBackgrounds.stream()
                .map(schoolBackground -> {
                    SchoolBackgroundDTO dto = new SchoolBackgroundDTO();
                    dto.setId(schoolBackground.getId());
                    dto.setBackground(schoolBackground.getBackground());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public SchoolBackgroundDTO getSchoolBackgroundById(Long id) {
        SchoolBackground schoolBackground = schoolBackgroundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolBackground not found with id: " + id));
        SchoolBackgroundDTO dto = new SchoolBackgroundDTO();
        dto.setId(schoolBackground.getId());
        dto.setBackground(schoolBackground.getBackground());
        return dto;
    }

    public SchoolBackgroundDTO updateSchoolBackground(Long id, SchoolBackgroundDTO schoolBackgroundDTO) {
        // Validate input
        if (schoolBackgroundDTO.getBackground() == null || schoolBackgroundDTO.getBackground().isEmpty()) {
            throw new IllegalArgumentException("Background cannot be empty");
        }
        SchoolBackground existing = schoolBackgroundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolBackground not found with id: " + id));
        // Check for duplicate (excluding self)
        if (!existing.getBackground().equals(schoolBackgroundDTO.getBackground()) &&
                schoolBackgroundRepository.findByBackground(schoolBackgroundDTO.getBackground()).isPresent()) {
            throw new DataIntegrityViolationException("Background '" + schoolBackgroundDTO.getBackground() + "' already exists");
        }
        try {
            existing.setBackground(schoolBackgroundDTO.getBackground());
            schoolBackgroundRepository.save(existing);
            return schoolBackgroundDTO;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to update school background due to database constraint");
        }
    }

    public void deleteSchoolBackground(Long id) {
        // Check if exists
        if (!schoolBackgroundRepository.existsById(id)) {
            throw new ResourceNotFoundException("SchoolBackground not found with id: " + id);
        }
        try {
            schoolBackgroundRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Cannot delete school background due to existing dependencies");
        }
    }
}