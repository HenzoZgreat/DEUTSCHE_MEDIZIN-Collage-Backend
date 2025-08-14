package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.EHEECE_G12DTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.EHEECE_G12;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentDetails;
import Henok.example.DeutscheCollageBack_endAPI.Repository.EHEECE_G12Repository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.StudentDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EHEECE_G12Service {

    @Autowired
    private EHEECE_G12Repository eheeceG12Repository;

    @Autowired
    private StudentDetailsRepository studentRepository;

    public void addEHEECE_G12s(List<EHEECE_G12DTO> dtos) {
        List<EHEECE_G12> entities = dtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
        eheeceG12Repository.saveAll(entities);
    }

    public List<EHEECE_G12> getAllEHEECE_G12() {
        return eheeceG12Repository.findAll();
    }

    public void addEHEECE_G12(EHEECE_G12DTO dto) {
        EHEECE_G12 entity = mapToEntity(dto);
        eheeceG12Repository.save(entity);
    }

    public EHEECE_G12 getEHEECE_G12ById(Long id) {
        return eheeceG12Repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("EHEECE_G12 not found: " + id));
    }

    public void updateEHEECE_G12(Long id, EHEECE_G12DTO dto) {
        EHEECE_G12 entity = getEHEECE_G12ById(id);
        StudentDetails student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + dto.getStudentId()));
        entity.setStudent(student);
        entity.setNationalExamId(dto.getNationalExamId());
        entity.setYear(dto.getYear());
        entity.setSubject(dto.getSubject());
        entity.setScore(dto.getScore());
        entity.setPhoto(dto.getPhoto());
        eheeceG12Repository.save(entity);
    }

    public void deleteEHEECE_G12(Long id) {
        eheeceG12Repository.deleteById(id);
    }

    private EHEECE_G12 mapToEntity(EHEECE_G12DTO dto) {
        StudentDetails student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + dto.getStudentId()));
        return new EHEECE_G12(null, student, dto.getNationalExamId(), dto.getYear(), dto.getSubject(), dto.getScore(), dto.getPhoto());
    }
}