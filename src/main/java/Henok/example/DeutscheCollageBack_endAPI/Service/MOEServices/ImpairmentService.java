package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ImpairmentDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Impairment;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ImpairmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Modified ImpairmentService with validations, error handling, and new methods
// Uses DTO for input/output to decouple from entity
@Service
public class ImpairmentService {

    @Autowired
    private ImpairmentRepository impairmentRepository;

    // Add multiple impairments with validation
    // Checks for null/empty list, duplicate codes, and required fields
    public List<ImpairmentDTO> addImpairments(List<ImpairmentDTO> impairmentDTOs) {
        if (impairmentDTOs == null || impairmentDTOs.isEmpty()) {
            throw new IllegalArgumentException("Impairment list cannot be null or empty");
        }

        List<Impairment> impairments = new ArrayList<>();
        for (ImpairmentDTO dto : impairmentDTOs) {
            validateDTO(dto);
            if (impairmentRepository.existsByImpairmentCode(dto.getDisabilityCode())) {
                throw new DataIntegrityViolationException("Impairment with code " + dto.getDisabilityCode() + " already exists");
            }
            impairments.add(mapToEntity(dto));
        }

        List<Impairment> savedImpairments = impairmentRepository.saveAll(impairments);
        return savedImpairments.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Add a single impairment with validation
    // Similar checks as bulk add
    public ImpairmentDTO addSingleImpairment(ImpairmentDTO impairmentDTO) {
        if (impairmentDTO == null) {
            throw new IllegalArgumentException("Impairment DTO cannot be null");
        }
        validateDTO(impairmentDTO);
        if (impairmentRepository.existsByImpairmentCode(impairmentDTO.getDisabilityCode())) {
            throw new DataIntegrityViolationException("Impairment with code " + impairmentDTO.getDisabilityCode() + " already exists");
        }
        Impairment impairment = mapToEntity(impairmentDTO);
        Impairment savedImpairment = impairmentRepository.save(impairment);
        return mapToDTO(savedImpairment);
    }

    // Retrieve all impairments as DTOs
    // Returns empty list if none exist (no exception thrown)
    public List<ImpairmentDTO> getAllImpairments() {
        List<Impairment> impairments = impairmentRepository.findAll();
        return impairments.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Retrieve impairment by code as DTO
    // Throws ResourceNotFoundException if not found
    public ImpairmentDTO getImpairmentByCode(String impairmentCode) {
        if (impairmentCode == null || impairmentCode.isEmpty()) {
            throw new IllegalArgumentException("Impairment code cannot be null or empty");
        }
        Impairment impairment = impairmentRepository.findById(impairmentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Impairment not found with code: " + impairmentCode));
        return mapToDTO(impairment);
    }

    // Remove impairment by code
    // Throws ResourceNotFoundException if not found; may throw DataIntegrityViolation if dependencies exist
    public void removeImpairment(String impairmentCode) {
        if (impairmentCode == null || impairmentCode.isEmpty()) {
            throw new IllegalArgumentException("Impairment code cannot be null or empty");
        }
        Impairment impairment = impairmentRepository.findById(impairmentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Impairment not found with code: " + impairmentCode));
        try {
            impairmentRepository.delete(impairment);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Cannot delete impairment due to existing references");
        }
    }

    // Validate DTO fields
    // Ensures required fields are present and not empty
    private void validateDTO(ImpairmentDTO dto) {
        if (dto.getDisabilityCode() == null || dto.getDisabilityCode().isEmpty()) {
            throw new IllegalArgumentException("Disability code cannot be null or empty");
        }
        if (dto.getDisability() == null || dto.getDisability().isEmpty()) {
            throw new IllegalArgumentException("Disability description cannot be null or empty");
        }
    }

    // Map DTO to Entity
    // Copies fields; note field name mismatch (disabilityCode -> impairmentCode)
    private Impairment mapToEntity(ImpairmentDTO dto) {
        Impairment impairment = new Impairment();
        impairment.setImpairmentCode(dto.getDisabilityCode());
        impairment.setImpairment(dto.getDisability());
        return impairment;
    }

    // Map Entity to DTO
    // Copies fields back; used for responses
    private ImpairmentDTO mapToDTO(Impairment impairment) {
        ImpairmentDTO dto = new ImpairmentDTO();
        dto.setDisabilityCode(impairment.getImpairmentCode());
        dto.setDisability(impairment.getImpairment());
        return dto;
    }
}