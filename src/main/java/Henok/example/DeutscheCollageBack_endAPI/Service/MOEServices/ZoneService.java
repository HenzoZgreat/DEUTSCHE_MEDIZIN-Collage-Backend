package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;


import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Region;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.RegionRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ZoneService {

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private RegionRepository regionRepository;

    public List<Zone> addMultipleZones(List<Zone> zones) {
        List<Zone> savedZones = new ArrayList<>();

        for (Zone zone : zones) {
            if (zoneRepository.existsByZoneCode(zone.getZoneCode())) {
                throw new DataIntegrityViolationException("Zone with code " + zone.getZoneCode() + " already exists");
            }

            if (zone.getRegion() == null || zone.getRegion().getRegionCode() == null) {
                throw new IllegalArgumentException("Region is required for Zone");
            }

            Region region = regionRepository.findByRegionCode(zone.getRegion().getRegionCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Region with code " + zone.getRegion().getRegionCode() + " not found"));
            zone.setRegion(region);

            savedZones.add(zoneRepository.save(zone));
        }

        return savedZones;
    }

    public Zone findByZoneCode(String zoneCode) {
        return zoneRepository.findByZoneCode(zoneCode)
                .orElseThrow(() -> new ResourceNotFoundException("Zone with code " + zoneCode + " not found"));
    }

    public List<Zone> findAll() {
        return zoneRepository.findAll();
    }
}