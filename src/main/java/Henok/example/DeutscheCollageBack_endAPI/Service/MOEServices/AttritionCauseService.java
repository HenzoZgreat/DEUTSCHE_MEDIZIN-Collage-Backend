package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.AttritionCauseDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AttritionCause;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.AttritionCauseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttritionCauseService {

    private final AttritionCauseRepository repository;

    // === CREATE ===
    public List<AttritionCauseDTO> addAttritionCauses(List<AttritionCauseDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Attrition cause list cannot be null or empty");
        }

        List<AttritionCause> entities = new ArrayList<>();
        for (AttritionCauseDTO dto : dtos) {
            validateDto(dto);
            if (repository.existsByAttritionCauseIgnoreCase(dto.getAttritionCause().trim())) {
                throw new DataIntegrityViolationException(
                        "Attrition cause '" + dto.getAttritionCause() + "' already exists");
            }
            AttritionCause entity = new AttritionCause();
            entity.setAttritionCause(dto.getAttritionCause().trim());
            entities.add(entity);
        }

        return repository.saveAll(entities).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AttritionCauseDTO addSingle(AttritionCauseDTO dto) {
        validateDto(dto);
        String cause = dto.getAttritionCause().trim();
        if (repository.existsByAttritionCauseIgnoreCase(cause)) {
            throw new DataIntegrityViolationException("Attrition cause '" + cause + "' already exists");
        }
        AttritionCause entity = new AttritionCause();
        entity.setAttritionCause(cause);
        return toDto(repository.save(entity));
    }

    // === READ ===
    public List<AttritionCauseDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AttritionCauseDTO getById(Long id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Attrition cause with ID " + id + " not found"));
    }

    // === UPDATE ===
    public AttritionCauseDTO update(Long id, AttritionCauseDTO dto) {
        validateDto(dto);
        String newCause = dto.getAttritionCause().trim();

        AttritionCause existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attrition cause with ID " + id + " not found"));

        // Check if new value conflicts with another record
        if (!existing.getAttritionCause().equalsIgnoreCase(newCause) &&
                repository.existsByAttritionCauseIgnoreCase(newCause)) {
            throw new DataIntegrityViolationException("Attrition cause '" + newCause + "' already exists");
        }

        existing.setAttritionCause(newCause);
        return toDto(repository.save(existing));
    }

    // === DELETE ===
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Attrition cause with ID " + id + " not found");
        }
        repository.deleteById(id);
    }

    // === Helpers ===
    private void validateDto(AttritionCauseDTO dto) {
        if (dto.getAttritionCause() == null || dto.getAttritionCause().trim().isEmpty()) {
            throw new IllegalArgumentException("Attrition cause name is required");
        }
    }

    private AttritionCauseDTO toDto(AttritionCause entity) {
        AttritionCauseDTO dto = new AttritionCauseDTO();
        dto.setAttritionCause(entity.getAttritionCause());
        return dto;
    }
}