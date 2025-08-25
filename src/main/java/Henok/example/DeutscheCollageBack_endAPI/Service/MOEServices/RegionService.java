package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.RegionDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Region;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionService {

    @Autowired
    private RegionRepository regionRepository;

    public List<Region> addMultipleRegions(List<Region> regions) {
        List<Region> savedRegions = new ArrayList<>();

        for (Region region : regions) {
            if (regionRepository.existsByRegionCode(region.getRegionCode())) {
                throw new DataIntegrityViolationException("Region with code " + region.getRegionCode() + " already exists");
            }

            savedRegions.add(regionRepository.save(region));
        }

        return savedRegions;
    }

    public Region findByRegionCode(String regionCode) {
        return regionRepository.findByRegionCode(regionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Region with code " + regionCode + " not found"));
    }

    public List<Region> findAll() {
        return regionRepository.findAll();
    }
}