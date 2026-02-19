package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ProgramLevelCreateDto;
import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ProgramLevelUpdateDto;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramLevel;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ProgramLevelRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ProgramModalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// src/main/java/com/yourorg/service/ProgramLevelService.java
@Service
@RequiredArgsConstructor
public class ProgramLevelService {

    private final ProgramLevelRepository repository;
    private final DepartmentRepo departmentRepository; // for checking references
    private final ProgramModalityRepository programModalityRepository; // for checking references

    /** Create a new program level */
    public ProgramLevel create(ProgramLevelCreateDto dto) {
        String code = dto.getCode().trim().toUpperCase();

        // ---- validation -------------------------------------------------
        if (repository.existsByCodeIgnoreCase(code)) {
            throw new IllegalArgumentException(
                    "Program level with code '" + code + "' already exists");
        }

        ProgramLevel entity = new ProgramLevel();
        entity.setCode(code);
        entity.setName(dto.getName().trim());
        entity.setRemark(dto.getRemark());
        entity.setActive(true);               // new levels are active by default
        return repository.save(entity);
    }

    /** Update an existing program level */
    public ProgramLevel update(String code, ProgramLevelUpdateDto dto) {
        ProgramLevel entity = repository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Program level with code '" + code + "' not found"));

        entity.setName(dto.getName().trim());
        entity.setRemark(dto.getRemark());
        entity.setActive(dto.getActive());
        return repository.save(entity);
    }

    // ────── GET ALL ──────
    public List<ProgramLevel> getAll() {
        return repository.findAll();
    }

    // ────── GET BY CODE ──────
    public ProgramLevel getByCode(String code) {
        return repository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("Program level with code '" + code + "' not found"));
    }

    /**
     * Deletes a program level ONLY if it is not referenced by any department or program modality.
     * Throws exception with clear message if deletion is blocked.
     */
    @Transactional
    public void deleteByCode(String code) {
        ProgramLevel level = repository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Program level with code '" + code + "' not found"));

        // Check if this level is still used
        boolean usedByDepartment = departmentRepository.existsByProgramLevel(level);
        boolean usedByModality   = programModalityRepository.existsByProgramLevel(level);

        if (usedByDepartment || usedByModality) {
            throw new BadRequestException(
                    "Cannot delete program level '" + code + "'. " +
                            "It is still referenced by one or more departments or program modalities. " +
                            "Please remove or reassign those references first."
            );
        }

        // Safe to delete
        repository.delete(level);
    }
}
