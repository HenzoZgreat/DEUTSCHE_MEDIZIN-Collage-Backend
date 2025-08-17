package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;



import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.CountryDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Country;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    public void addCountries(List<CountryDTO> countryDTOs) {
        List<Country> countries = countryDTOs.stream()
                .map(dto -> new Country(dto.getCountryCode(), dto.getCountry()))
                .collect(Collectors.toList());
        countryRepository.saveAll(countries);
    }

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }
}