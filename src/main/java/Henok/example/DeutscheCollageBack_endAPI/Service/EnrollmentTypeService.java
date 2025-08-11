package Henok.example.DeutscheCollageBack_endAPI.Service;


import Henok.example.DeutscheCollageBack_endAPI.DTO.EnrollmentTypeDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.EnrollmentType;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.EnrollmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentTypeService {

    @Autowired
    private EnrollmentTypeRepository enrollmentTypeRepository;

    public void addEnrollmentTypes(List<EnrollmentTypeDTO> enrollmentTypeDTOs) {
        List<EnrollmentType> enrollmentTypes = enrollmentTypeDTOs.stream()
                .map(dto -> new EnrollmentType(dto.getEnrollmentTypeCode(), dto.getEnrollmentTypeName()))
                .collect(Collectors.toList());
        enrollmentTypeRepository.saveAll(enrollmentTypes);
    }

    public List<EnrollmentType> getAllEnrollmentTypes() {
        return enrollmentTypeRepository.findAll();
    }
}