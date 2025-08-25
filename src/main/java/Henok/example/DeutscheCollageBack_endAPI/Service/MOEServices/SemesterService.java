package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.SemesterDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
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
        if (semesterDTOs == null || semesterDTOs.isEmpty()) {
            throw new IllegalArgumentException("Semester list cannot be null or empty");
        }

        List<Semester> semesters = semesterDTOs.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        for (Semester semester : semesters) {
            validateSemester(semester);
            if (semesterRepository.existsById(semester.getAcademicPeriodCode())) {
                throw new IllegalArgumentException("Academic period code already exists: " + semester.getAcademicPeriodCode());
            }
        }

        semesterRepository.saveAll(semesters);
    }

    public void addSemester(SemesterDTO semesterDTO) {
        if (semesterDTO == null) {
            throw new IllegalArgumentException("Semester DTO cannot be null");
        }

        Semester semester = mapToEntity(semesterDTO);
        validateSemester(semester);

        if (semesterRepository.existsById(semester.getAcademicPeriodCode())) {
            throw new IllegalArgumentException("Academic period code already exists: " + semester.getAcademicPeriodCode());
        }

        semesterRepository.save(semester);
    }

    public List<Semester> getAllSemesters() {
        List<Semester> semesters = semesterRepository.findAll();
        if (semesters.isEmpty()) {
            throw new ResourceNotFoundException("No semesters found");
        }
        return semesters;
    }

    public Semester getSemesterById(String id) {
        return semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id: " + id));
    }

    public void updateSemester(String id, SemesterDTO semesterDTO) {
        if (semesterDTO == null) {
            throw new IllegalArgumentException("Semester DTO cannot be null");
        }

        Semester existingSemester = getSemesterById(id);
        String newCode = semesterDTO.getAcademicPeriodCode();

        if (!existingSemester.getAcademicPeriodCode().equals(newCode) &&
                semesterRepository.existsById(newCode)) {
            throw new IllegalArgumentException("Academic period code already exists: " + newCode);
        }

        existingSemester.setAcademicPeriodCode(newCode);
        existingSemester.setAcademicPeriod(semesterDTO.getAcademicPeriod());
        validateSemester(existingSemester);

        semesterRepository.save(existingSemester);
    }

    public void deleteSemester(String id) {
        if (!semesterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Semester not found with id: " + id);
        }
        semesterRepository.deleteById(id);
    }

    private Semester mapToEntity(SemesterDTO dto) {
        return new Semester(dto.getAcademicPeriodCode(), dto.getAcademicPeriod());
    }

    private void validateSemester(Semester semester) {
        if (semester.getAcademicPeriodCode() == null || semester.getAcademicPeriodCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Academic period code cannot be null or empty");
        }
        if (semester.getAcademicPeriod() == null || semester.getAcademicPeriod().trim().isEmpty()) {
            throw new IllegalArgumentException("Academic period cannot be null or empty");
        }
    }
}