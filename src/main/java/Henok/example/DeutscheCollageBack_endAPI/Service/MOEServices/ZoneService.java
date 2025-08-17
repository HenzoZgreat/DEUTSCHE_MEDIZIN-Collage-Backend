package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;

import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.ZoneDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Region;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.RegionRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneService {

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private RegionRepository regionRepository;

    public void addZones(List<ZoneDTO> zoneDTOs) {
        List<Zone> zones = zoneDTOs.stream()
                .map(dto -> {
                    Region region = regionRepository.findById(dto.getRegionCode())
                            .orElseThrow(() -> new IllegalArgumentException("Region not found: " + dto.getRegionCode()));
                    return new Zone(dto.getZoneCode(), dto.getZone(), region);
                })
                .collect(Collectors.toList());
        zoneRepository.saveAll(zones);
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }
}