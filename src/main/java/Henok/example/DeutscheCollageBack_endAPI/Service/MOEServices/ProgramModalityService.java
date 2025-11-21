package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ProgramModalityDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramLevel;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramModality;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ProgramLevelRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ProgramModalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramModalityService {

    @Autowired
    private ProgramModalityRepository programModalityRepository;
    @Autowired
    private ProgramLevelRepository programLevelRepository;

    // Save single
    public ProgramModalityDTO save(ProgramModalityDTO dto) {
        validateDto(dto);
        ProgramLevel programLevel = getActiveProgramLevel(dto.getProgramLevelCode());

        if (programModalityRepository.existsByModalityCode(dto.getModalityCode())) {
            throw new DataIntegrityViolationException("ProgramModality with code " + dto.getModalityCode() + " already exists");
        }

        ProgramModality entity = toEntity(dto, programLevel);
        ProgramModality saved = programModalityRepository.save(entity);
        return toDto(saved);
    }

    // Save multiple
    public List<ProgramModalityDTO> saveMultiple(List<ProgramModalityDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Program modality list cannot be null or empty");
        }

        List<ProgramModalityDTO> savedDtos = new ArrayList<>();
        for (ProgramModalityDTO dto : dtos) {
            savedDtos.add(save(dto)); // Reuse single save with full validation
        }
        return savedDtos;
    }

    public ProgramModalityDTO findByModalityCode(String modalityCode) {
        ProgramModality entity = programModalityRepository.findByModalityCode(modalityCode)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramModality with code " + modalityCode + " not found"));
        return toDto(entity);
    }

    public List<ProgramModalityDTO> findAll() {
        return programModalityRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProgramModalityDTO update(String modalityCode, ProgramModalityDTO dto) {
        validateDto(dto);
        ProgramModality existing = programModalityRepository.findByModalityCode(modalityCode)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramModality with code " + modalityCode + " not found"));

        if (!modalityCode.equals(dto.getModalityCode()) &&
                programModalityRepository.existsByModalityCode(dto.getModalityCode())) {
            throw new DataIntegrityViolationException("ProgramModality with code " + dto.getModalityCode() + " already exists");
        }

        ProgramLevel programLevel = getActiveProgramLevel(dto.getProgramLevelCode());

        existing.setModalityCode(dto.getModalityCode());
        existing.setModality(dto.getModality());
        existing.setProgramLevel(programLevel);

        return toDto(programModalityRepository.save(existing));
    }

    public void delete(String modalityCode) {
        ProgramModality entity = programModalityRepository.findByModalityCode(modalityCode)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramModality with code " + modalityCode + " not found"));
        programModalityRepository.delete(entity);
    }

    // --- Helper Methods ---

    private void validateDto(ProgramModalityDTO dto) {
        if (dto.getModalityCode() == null || dto.getModalityCode().trim().isEmpty()) {
            throw new IllegalArgumentException("modalityCode is required");
        }
        if (dto.getModality() == null || dto.getModality().trim().isEmpty()) {
            throw new IllegalArgumentException("modality name is required");
        }
        if (dto.getProgramLevelCode() == null || dto.getProgramLevelCode().trim().isEmpty()) {
            throw new IllegalArgumentException("programLevelCode is required");
        }
    }

    private ProgramLevel getActiveProgramLevel(String code) {
        return programLevelRepository.findById(code)
                .filter(ProgramLevel::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Active ProgramLevel with code " + code + " not found or is inactive"));
    }

    private ProgramModality toEntity(ProgramModalityDTO dto, ProgramLevel programLevel) {
        ProgramModality entity = new ProgramModality();
        entity.setModalityCode(dto.getModalityCode());
        entity.setModality(dto.getModality());
        entity.setProgramLevel(programLevel);
        return entity;
    }

    private ProgramModalityDTO toDto(ProgramModality entity) {
        ProgramModalityDTO dto = new ProgramModalityDTO();
        dto.setModalityCode(entity.getModalityCode());
        dto.setModality(entity.getModality());
        dto.setProgramLevelCode(entity.getProgramLevel().getCode());
        return dto;
    }
}