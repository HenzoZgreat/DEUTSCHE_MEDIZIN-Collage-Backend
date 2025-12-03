package Henok.example.DeutscheCollageBack_endAPI.Service;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.AcademicYearDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicYear;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.AcademicYearRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicYearService {

    private final AcademicYearRepo repository;

    // === CREATE ===
    public List<AcademicYearDTO> addAcademicYears(List<AcademicYearDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Academic year list cannot be null or empty");
        }

        List<AcademicYear> entities = new ArrayList<>();
        for (AcademicYearDTO dto : dtos) {
            validateDto(dto);
            if (repository.existsByYearCode(dto.getAcademicYearCode())) {
                throw new DataIntegrityViolationException(
                        "AcademicYear with code " + dto.getAcademicYearCode() + " already exists");
            }
            entities.add(toEntity(dto));
        }

        return repository.saveAll(entities).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AcademicYearDTO addSingle(AcademicYearDTO dto) {
        validateDto(dto);
        if (repository.existsByYearCode(dto.getAcademicYearCode())) {
            throw new DataIntegrityViolationException(
                    "AcademicYear with code " + dto.getAcademicYearCode() + " already exists");
        }
        return toDto(repository.save(toEntity(dto)));
    }

    // === READ ===
    public List<AcademicYearDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AcademicYearDTO getByCode(String yearCode) {
        return repository.findById(yearCode)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AcademicYear with code " + yearCode + " not found"));
    }

    // === UPDATE ===
    public AcademicYearDTO update(String yearCode, AcademicYearDTO dto) {
        validateDto(dto);
        AcademicYear existing = repository.findById(yearCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AcademicYear with code " + yearCode + " not found"));

        // Prevent changing the PK to an already existing one
        if (!yearCode.equals(dto.getAcademicYearCode()) &&
                repository.existsByYearCode(dto.getAcademicYearCode())) {
            throw new DataIntegrityViolationException(
                    "AcademicYear with code " + dto.getAcademicYearCode() + " already exists");
        }

        existing.setYearCode(dto.getAcademicYearCode());
        existing.setAcademicYearGC(dto.getAcademicYearGC());
        existing.setAcademicYearEC(dto.getAcademicYearEC());

        return toDto(repository.save(existing));
    }

    // === DELETE ===
    public void delete(String yearCode) {
        if (!repository.existsById(yearCode)) {
            throw new ResourceNotFoundException("AcademicYear with code " + yearCode + " not found");
        }
        repository.deleteById(yearCode);
    }

    // === Helpers ===
    private void validateDto(AcademicYearDTO dto) {
        if (dto.getAcademicYearCode() == null || dto.getAcademicYearCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Academic Year Code is required");
        }
        if (dto.getAcademicYearGC() == null || dto.getAcademicYearGC().trim().isEmpty()) {
            throw new IllegalArgumentException("Academic Year (GC) is required");
        }
    }

    private AcademicYear toEntity(AcademicYearDTO dto) {
        return new AcademicYear(
                dto.getAcademicYearCode(),
                dto.getAcademicYearGC(),
                dto.getAcademicYearEC()
        );
    }

    private AcademicYearDTO toDto(AcademicYear entity) {
        return new AcademicYearDTO(
                entity.getYearCode(),
                entity.getAcademicYearGC(),
                entity.getAcademicYearEC()
        );
    }
}