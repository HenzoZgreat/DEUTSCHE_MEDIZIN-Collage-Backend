package Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramLevelRepository extends JpaRepository<ProgramLevel, String> {
    boolean existsByCodeIgnoreCase(String code);

    Optional<ProgramLevel> findByCodeIgnoreCase(String code);
}