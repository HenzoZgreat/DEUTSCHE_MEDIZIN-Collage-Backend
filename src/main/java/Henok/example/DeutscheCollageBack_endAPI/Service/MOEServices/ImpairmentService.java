package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ImpairmentDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Impairment;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ImpairmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImpairmentService {

    @Autowired
    private ImpairmentRepository impairmentRepository;

    public void addImpairments(List<ImpairmentDTO> impairmentDTOs) {
        List<Impairment> impairments = impairmentDTOs.stream()
                .map(dto -> {
                    Impairment impairment = new Impairment();
                    impairment.setImpairmentCode(dto.getDisabilityCode());
                    impairment.setImpairment(dto.getDisability());
                    return impairment;
                })
                .collect(Collectors.toList());
        impairmentRepository.saveAll(impairments);
    }

    public List<Impairment> getAllImpairments() {
        return impairmentRepository.findAll();
    }
}