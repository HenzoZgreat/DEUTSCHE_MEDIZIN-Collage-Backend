package Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, String> {

    boolean existsByZoneCode(String zoneCode);

    Optional<Zone> findByZoneCode(String zoneCode);

    List<Zone> findByRegionRegionCode(String regionCode);

}