package Henok.example.DeutscheCollageBack_endAPI.Service.MOEServices;



import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Country;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository repository;

    // === CREATE ===
    public List<Country> addMultiple(List<Country> countries) {
        if (countries == null || countries.isEmpty()) {
            throw new IllegalArgumentException("Country list cannot be null or empty");
        }

        List<Country> saved = new ArrayList<>();
        for (Country c : countries) {
            validate(c);
            if (repository.existsByCountryCode(c.getCountryCode())) {
                throw new DataIntegrityViolationException(
                        "Country with code '" + c.getCountryCode() + "' already exists");
            }
            saved.add(repository.save(c));
        }
        return saved;
    }

    public Country addSingle(Country country) {
        validate(country);
        if (repository.existsByCountryCode(country.getCountryCode())) {
            throw new DataIntegrityViolationException(
                    "Country with code '" + country.getCountryCode() + "' already exists");
        }
        return repository.save(country);
    }

    // === READ ===
    public List<Country> findAll() {
        return repository.findAll();
    }

    public Country findByCode(String countryCode) {
        return repository.findByCountryCode(countryCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Country with code '" + countryCode + "' not found"));
    }

    // === UPDATE ===
    public Country update(String countryCode, Country updated) {
        validate(updated);
        Country existing = repository.findByCountryCode(countryCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Country with code '" + countryCode + "' not found"));

        // Prevent changing code to an existing one
        if (!countryCode.equals(updated.getCountryCode()) &&
                repository.existsByCountryCode(updated.getCountryCode())) {
            throw new DataIntegrityViolationException(
                    "Country with code '" + updated.getCountryCode() + "' already exists");
        }

        existing.setCountryCode(updated.getCountryCode());
        existing.setCountry(updated.getCountry());
        return repository.save(existing);
    }

    // === DELETE ===
    public void delete(String countryCode) {
        if (!repository.existsByCountryCode(countryCode)) {
            throw new ResourceNotFoundException("Country with code '" + countryCode + "' not found");
        }
        repository.deleteById(countryCode);
    }

    private void validate(Country c) {
        if (c.getCountryCode() == null || c.getCountryCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Country code is required");
        }
        if (c.getCountry() == null || c.getCountry().trim().isEmpty()) {
            throw new IllegalArgumentException("Country name is required");
        }
    }
}