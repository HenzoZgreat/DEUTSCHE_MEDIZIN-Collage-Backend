package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramModality;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ProgramModalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProgramModalityService {

    @Autowired
    private ProgramModalityRepository programModalityRepository;

    public ProgramModality save(ProgramModality programModality) {
        return programModalityRepository.save(programModality);
    }

    public List<ProgramModality> saveMultiple(List<ProgramModality> programModalities) {
        List<ProgramModality> savedProgramModalities = new ArrayList<>();
        for (ProgramModality programModality : programModalities) {
            if (programModalityRepository.existsByModalityCode(programModality.getModalityCode())) {
                throw new DataIntegrityViolationException("ProgramModality with code " + programModality.getModalityCode() + " already exists");
            }
            savedProgramModalities.add(programModalityRepository.save(programModality));
        }
        return savedProgramModalities;
    }

    public ProgramModality findByModalityCode(String modalityCode) {
        return programModalityRepository.findByModalityCode(modalityCode)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramModality with code " + modalityCode + " not found"));
    }

    public List<ProgramModality> findAll() {
        return programModalityRepository.findAll();
    }

    public ProgramModality update(String modalityCode, ProgramModality programModality) {
        ProgramModality existingProgramModality = programModalityRepository.findByModalityCode(modalityCode)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramModality with code " + modalityCode + " not found"));

        if (!modalityCode.equals(programModality.getModalityCode()) &&
                programModalityRepository.existsByModalityCode(programModality.getModalityCode())) {
            throw new DataIntegrityViolationException("ProgramModality with code " + programModality.getModalityCode() + " already exists");
        }

        existingProgramModality.setModalityCode(programModality.getModalityCode());
        existingProgramModality.setModality(programModality.getModality());
        return programModalityRepository.save(existingProgramModality);
    }

    public void delete(String modalityCode) {
        ProgramModality programModality = programModalityRepository.findByModalityCode(modalityCode)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramModality with code " + modalityCode + " not found"));
        programModalityRepository.delete(programModality);
    }

    public boolean existsByModalityCode(String modalityCode) {
        return programModalityRepository.existsByModalityCode(modalityCode);
    }
}