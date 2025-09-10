package Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Impairment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ImpairmentRepository extends JpaRepository<Impairment, String> {
    Optional<Impairment> findByImpairmentCode(String impairmentCode);
}
