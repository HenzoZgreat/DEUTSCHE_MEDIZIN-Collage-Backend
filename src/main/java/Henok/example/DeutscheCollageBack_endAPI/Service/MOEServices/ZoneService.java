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
import java.util.Collections;
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

    // Find zone by code
    // Throws ResourceNotFoundException if not found
    public Zone findByZoneCode(String zoneCode) {
        if (zoneCode == null || zoneCode.isEmpty()) {
            throw new IllegalArgumentException("Zone code cannot be null or empty");
        }
        return zoneRepository.findByZoneCode(zoneCode)
                .orElseThrow(() -> new ResourceNotFoundException("Zone with code " + zoneCode + " not found"));
    }

    // Retrieve all zones
    public List<Zone> findAll() {
        List<Zone> zones = zoneRepository.findAll();
        return zones.isEmpty() ? Collections.emptyList() : zones;
    }

    // Find zones by region code
    // Throws ResourceNotFoundException if none found
    public List<Zone> findByRegionCode(String regionCode) {
        if (regionCode == null || regionCode.isEmpty()) {
            throw new IllegalArgumentException("Region code cannot be null or empty");
        }
        List<Zone> zones = zoneRepository.findByRegionRegionCode(regionCode);
        if (zones.isEmpty()) {
            throw new ResourceNotFoundException("No zones found for region code: " + regionCode);
        }
        return zones;
    }
}