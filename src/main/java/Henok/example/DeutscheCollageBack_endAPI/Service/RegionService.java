package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.RegionDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Region;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionService {

    @Autowired
    private RegionRepository regionRepository;

    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    public void addRegions(List<RegionDTO> regionDTOs) {
        List<Region> regions = regionDTOs.stream()
                .filter(dto -> StringUtils.hasText(dto.getRegionCode())) // Ensure regionCode is not null or empty
                .map(dto -> new Region(
                        dto.getRegionCode(),
                        dto.getRegion(),
                        dto.getRegionType()
                ))
                .collect(Collectors.toList());
        regionRepository.saveAll(regions);
    }
}