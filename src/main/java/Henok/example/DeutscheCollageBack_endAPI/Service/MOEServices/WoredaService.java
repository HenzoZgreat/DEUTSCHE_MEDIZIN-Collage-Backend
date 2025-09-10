package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;


import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Woreda;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.WoredaRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class WoredaService {

    @Autowired
    private WoredaRepository woredaRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    // Add multiple woredas with validation
    // Ensures no duplicates and valid zone references
    public List<Woreda> addMultipleWoredas(List<Woreda> woredas) {
        if (woredas == null || woredas.isEmpty()) {
            throw new IllegalArgumentException("Woreda list cannot be null or empty");
        }

        List<Woreda> savedWoredas = new ArrayList<>();

        for (Woreda woreda : woredas) {
            if (woreda.getWoredaCode() == null || woreda.getWoredaCode().isEmpty()) {
                throw new IllegalArgumentException("Woreda code cannot be null or empty");
            }
            if (woredaRepository.existsByWoredaCode(woreda.getWoredaCode())) {
                throw new DataIntegrityViolationException("Woreda with code " + woreda.getWoredaCode() + " already exists");
            }

            if (woreda.getZone() == null || woreda.getZone().getZoneCode() == null) {
                throw new IllegalArgumentException("Zone is required for Woreda");
            }

            Zone zone = zoneRepository.findByZoneCode(woreda.getZone().getZoneCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Zone with code " + woreda.getZone().getZoneCode() + " not found"));
            woreda.setZone(zone);

            savedWoredas.add(woredaRepository.save(woreda));
        }

        return savedWoredas;
    }

    // Find woreda by code
    // Throws ResourceNotFoundException if not found
    public Woreda findByWoredaCode(String woredaCode) {
        if (woredaCode == null || woredaCode.isEmpty()) {
            throw new IllegalArgumentException("Woreda code cannot be null or empty");
        }
        return woredaRepository.findByWoredaCode(woredaCode)
                .orElseThrow(() -> new ResourceNotFoundException("Woreda with code " + woredaCode + " not found"));
    }

    // Retrieve all woredas
    public List<Woreda> findAll() {
        List<Woreda> woredas = woredaRepository.findAll();
        return woredas.isEmpty() ? Collections.emptyList() : woredas;
    }

    // Find woredas by zone code
    // Throws ResourceNotFoundException if none found
    public List<Woreda> findByZoneCode(String zoneCode) {
        if (zoneCode == null || zoneCode.isEmpty()) {
            throw new IllegalArgumentException("Zone code cannot be null or empty");
        }
        List<Woreda> woredas = woredaRepository.findByZoneZoneCode(zoneCode);
        if (woredas.isEmpty()) {
            throw new ResourceNotFoundException("No woredas found for zone code: " + zoneCode);
        }
        return woredas;
    }

    // Find woredas by region code
    // Throws ResourceNotFoundException if none found
    public List<Woreda> findByRegionCode(String regionCode) {
        if (regionCode == null || regionCode.isEmpty()) {
            throw new IllegalArgumentException("Region code cannot be null or empty");
        }
        List<Woreda> woredas = woredaRepository.findByZoneRegionRegionCode(regionCode);
        if (woredas.isEmpty()) {
            throw new ResourceNotFoundException("No woredas found for region code: " + regionCode);
        }
        return woredas;
    }
}