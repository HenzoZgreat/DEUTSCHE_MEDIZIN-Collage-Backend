package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.AreaTypeDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AreaType;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.AreaTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AreaTypeService {

    private final AreaTypeRepository repository;

    // === CREATE ===
    public List<AreaTypeDTO> addAreaTypes(List<AreaTypeDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Area type list cannot be null or empty");
        }

        List<AreaType> entities = new ArrayList<>();
        for (AreaTypeDTO dto : dtos) {
            validateDto(dto);
            if (repository.existsByAreaTypeCode(dto.getAreaTypeCode())) {
                throw new DataIntegrityViolationException(
                        "AreaType with code " + dto.getAreaTypeCode() + " already exists");
            }
            entities.add(toEntity(dto));
        }

        return repository.saveAll(entities).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AreaTypeDTO addSingle(AreaTypeDTO dto) {
        validateDto(dto);
        if (repository.existsByAreaTypeCode(dto.getAreaTypeCode())) {
            throw new DataIntegrityViolationException(
                    "AreaType with code " + dto.getAreaTypeCode() + " already exists");
        }
        return toDto(repository.save(toEntity(dto)));
    }

    // === READ ===
    public List<AreaTypeDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AreaTypeDTO getByCode(String code) {
        return repository.findById(code)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AreaType with code " + code + " not found"));
    }

    // === UPDATE ===
    public AreaTypeDTO update(String code, AreaTypeDTO dto) {
        validateDto(dto);
        AreaType existing = repository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AreaType with code " + code + " not found"));

        if (!code.equals(dto.getAreaTypeCode()) &&
                repository.existsByAreaTypeCode(dto.getAreaTypeCode())) {
            throw new DataIntegrityViolationException(
                    "AreaType with code " + dto.getAreaTypeCode() + " already exists");
        }

        existing.setAreaTypeCode(dto.getAreaTypeCode());
        existing.setAreaType(dto.getAreaType());

        return toDto(repository.save(existing));
    }

    // === DELETE ===
    public void delete(String code) {
        if (!repository.existsById(code)) {
            throw new ResourceNotFoundException("AreaType with code " + code + " not found");
        }
        repository.deleteById(code);
    }

    // === Helpers ===
    private void validateDto(AreaTypeDTO dto) {
        if (dto.getAreaTypeCode() == null || dto.getAreaTypeCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Area Type Code is required");
        }
        if (dto.getAreaType() == null || dto.getAreaType().trim().isEmpty()) {
            throw new IllegalArgumentException("Area Type name is required");
        }
    }

    private AreaType toEntity(AreaTypeDTO dto) {
        return new AreaType(dto.getAreaTypeCode(), dto.getAreaType());
    }

    private AreaTypeDTO toDto(AreaType entity) {
        return new AreaTypeDTO(entity.getAreaTypeCode(), entity.getAreaType());
    }
}