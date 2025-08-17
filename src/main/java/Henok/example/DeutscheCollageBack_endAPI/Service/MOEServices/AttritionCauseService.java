package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.AttritionCauseDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AttritionCause;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.AttritionCauseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttritionCauseService {

    @Autowired
    private AttritionCauseRepository attritionCauseRepository;

    public void addAttritionCauses(List<AttritionCauseDTO> attritionCauseDTOs) {
        List<AttritionCause> attritionCauses = attritionCauseDTOs.stream()
                .map(dto -> {
                    AttritionCause attritionCause = new AttritionCause();
                    attritionCause.setAttritionCause(dto.getAttritionCause());
                    return attritionCause;
                })
                .collect(Collectors.toList());
        attritionCauseRepository.saveAll(attritionCauses);
    }

    public List<AttritionCause> getAllAttritionCauses() {
        return attritionCauseRepository.findAll();
    }
}