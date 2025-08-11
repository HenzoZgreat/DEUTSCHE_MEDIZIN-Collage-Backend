package Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {
}
