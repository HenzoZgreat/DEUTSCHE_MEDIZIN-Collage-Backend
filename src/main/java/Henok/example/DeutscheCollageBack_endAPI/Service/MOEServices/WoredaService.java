package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;


import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.WoredaDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Woreda;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.WoredaRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WoredaService {

    @Autowired
    private WoredaRepository woredaRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    public List<Woreda> addMultipleWoredas(List<Woreda> woredas) {
        List<Woreda> savedWoredas = new ArrayList<>();

        for (Woreda woreda : woredas) {
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

    public Woreda findByWoredaCode(String woredaCode) {
        return woredaRepository.findByWoredaCode(woredaCode)
                .orElseThrow(() -> new ResourceNotFoundException("Woreda with code " + woredaCode + " not found"));
    }

    public List<Woreda> findAll() {
        return woredaRepository.findAll();
    }
}