package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramModality;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ProgramModalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramModalityService {

    @Autowired
    private ProgramModalityRepository programModalityRepository;

    public void addProgramModalities(List<ProgramModality> programModalities) {
        programModalityRepository.saveAll(programModalities);
    }

    public List<ProgramModality> getAllProgramModalities() {
        return programModalityRepository.findAll();
    }

    public void addProgramModality(ProgramModality programModality) {
        programModalityRepository.save(programModality);
    }

    public ProgramModality getProgramModalityById(String id) {
        return programModalityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Program modality not found: " + id));
    }

    public void updateProgramModality(String id, ProgramModality programModality) {
        ProgramModality existing = getProgramModalityById(id);
        existing.setModality(programModality.getModality());
        programModalityRepository.save(existing);
    }

    public void deleteProgramModality(String id) {
        programModalityRepository.deleteById(id);
    }
}