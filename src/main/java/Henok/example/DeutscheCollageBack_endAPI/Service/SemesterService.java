package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.SemesterDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.SemesterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SemesterService {

    @Autowired
    private SemesterRepo semesterRepository;

    public void addSemesters(List<SemesterDTO> semesterDTOs) {
        List<Semester> semesters = semesterDTOs.stream()
                .map(dto -> new Semester(dto.getAcademicPeriodCode(), dto.getAcademicPeriod()))
                .collect(Collectors.toList());
        semesterRepository.saveAll(semesters);
    }

    public List<Semester> getAllSemesters() {
        return semesterRepository.findAll();
    }
}