package Henok.example.DeutscheCollageBack_endAPI.Service;


import Henok.example.DeutscheCollageBack_endAPI.DTO.WoredaDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Woreda;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.WoredaRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WoredaService {

    @Autowired
    private WoredaRepository woredaRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    public void addWoredas(List<WoredaDTO> woredaDTOs) {
        List<Woreda> woredas = woredaDTOs.stream()
                .map(dto -> {
                    Zone zone = zoneRepository.findById(dto.getZoneCode())
                            .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + dto.getZoneCode()));
                    return new Woreda(dto.getWoredaCode(), dto.getWoreda(), zone);
                })
                .collect(Collectors.toList());
        woredaRepository.saveAll(woredas);
    }

    public List<Woreda> getAllWoredas() {
        return woredaRepository.findAll();
    }
}