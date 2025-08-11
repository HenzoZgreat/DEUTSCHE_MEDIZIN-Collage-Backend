package Henok.example.DeutscheCollageBack_endAPI.Service;


import Henok.example.DeutscheCollageBack_endAPI.DTO.AcademicYearDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicYear;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.AcademicYearRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcademicYearService {

    @Autowired
    private AcademicYearRepo academicYearRepository;

    public void addAcademicYears(List<AcademicYearDTO> academicYearDTOs) {
        List<AcademicYear> academicYears = academicYearDTOs.stream()
                .map(dto -> new AcademicYear(dto.getAcademicYearCode(), dto.getAcademicYearGC(), dto.getAcademicYearEC()))
                .collect(Collectors.toList());
        academicYearRepository.saveAll(academicYears);
    }

    public List<AcademicYear> getAllAcademicYears() {
        return academicYearRepository.findAll();
    }
}