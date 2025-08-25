package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.EnrollmentTypeDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.EnrollmentType;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.EnrollmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentTypeService {

    @Autowired
    private EnrollmentTypeRepository enrollmentTypeRepository;

    public EnrollmentType save(EnrollmentType enrollmentType) {
        return enrollmentTypeRepository.save(enrollmentType);
    }

    public List<EnrollmentType> saveMultiple(List<EnrollmentType> enrollmentTypes) {
        List<EnrollmentType> savedEnrollmentTypes = new ArrayList<>();
        for (EnrollmentType enrollmentType : enrollmentTypes) {
            if (enrollmentTypeRepository.existsByEnrollmentTypeCode(enrollmentType.getEnrollmentTypeCode())) {
                throw new DataIntegrityViolationException("EnrollmentType with code " + enrollmentType.getEnrollmentTypeCode() + " already exists");
            }
            savedEnrollmentTypes.add(enrollmentTypeRepository.save(enrollmentType));
        }
        return savedEnrollmentTypes;
    }

    public EnrollmentType findByEnrollmentTypeCode(String enrollmentTypeCode) {
        return enrollmentTypeRepository.findByEnrollmentTypeCode(enrollmentTypeCode)
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentType with code " + enrollmentTypeCode + " not found"));
    }

    public List<EnrollmentType> findAll() {
        return enrollmentTypeRepository.findAll();
    }

    public EnrollmentType update(String enrollmentTypeCode, EnrollmentType enrollmentType) {
        EnrollmentType existingEnrollmentType = enrollmentTypeRepository.findByEnrollmentTypeCode(enrollmentTypeCode)
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentType with code " + enrollmentTypeCode + " not found"));

        if (!enrollmentTypeCode.equals(enrollmentType.getEnrollmentTypeCode()) &&
                enrollmentTypeRepository.existsByEnrollmentTypeCode(enrollmentType.getEnrollmentTypeCode())) {
            throw new DataIntegrityViolationException("EnrollmentType with code " + enrollmentType.getEnrollmentTypeCode() + " already exists");
        }

        existingEnrollmentType.setEnrollmentTypeCode(enrollmentType.getEnrollmentTypeCode());
        existingEnrollmentType.setEnrollmentTypeName(enrollmentType.getEnrollmentTypeName());
        return enrollmentTypeRepository.save(existingEnrollmentType);
    }

    public void delete(String enrollmentTypeCode) {
        EnrollmentType enrollmentType = enrollmentTypeRepository.findByEnrollmentTypeCode(enrollmentTypeCode)
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentType with code " + enrollmentTypeCode + " not found"));
        enrollmentTypeRepository.delete(enrollmentType);
    }

    public boolean existsByEnrollmentTypeCode(String enrollmentTypeCode) {
        return enrollmentTypeRepository.existsByEnrollmentTypeCode(enrollmentTypeCode);
    }
}