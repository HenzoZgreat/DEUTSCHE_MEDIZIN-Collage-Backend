package Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Woreda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WoredaRepository extends JpaRepository<Woreda, String> {

    boolean existsByWoredaCode(String woredaCode);

    Optional<Woreda> findByWoredaCode(String woredaCode);
}