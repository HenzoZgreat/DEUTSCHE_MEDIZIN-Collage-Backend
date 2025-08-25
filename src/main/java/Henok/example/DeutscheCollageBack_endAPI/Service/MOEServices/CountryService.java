package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;



import Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs.CountryDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Country;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    public List<Country> addMultipleCountries(List<Country> countries) {
        List<Country> savedCountries = new ArrayList<>();

        for (Country country : countries) {
            if (countryRepository.existsByCountryCode(country.getCountryCode())) {
                throw new DataIntegrityViolationException("Country with code " + country.getCountryCode() + " already exists");
            }

            savedCountries.add(countryRepository.save(country));
        }

        return savedCountries;
    }

    public Country findByCountryCode(String countryCode) {
        return countryRepository.findByCountryCode(countryCode)
                .orElseThrow(() -> new ResourceNotFoundException("Country with code " + countryCode + " not found"));
    }

    public List<Country> findAll() {
        return countryRepository.findAll();
    }
}