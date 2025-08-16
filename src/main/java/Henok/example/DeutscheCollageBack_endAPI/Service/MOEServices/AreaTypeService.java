package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AreaTypeDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AreaType;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.AreaTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AreaTypeService {

    @Autowired
    private AreaTypeRepository areaTypeRepository;

    public void addAreaTypes(List<AreaTypeDTO> areaTypeDTOs) {
        List<AreaType> areaTypes = areaTypeDTOs.stream()
                .map(dto -> new AreaType(
                        dto.getAreaTypeCode(),
                        dto.getAreaType()
                ))
                .collect(Collectors.toList());
        areaTypeRepository.saveAll(areaTypes);
    }

    public List<AreaType> getAllAreaTypes() {
        return areaTypeRepository.findAll();
    }
}