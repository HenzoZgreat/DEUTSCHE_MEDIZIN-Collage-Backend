package Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramLevel;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramModality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramModalityRepository extends JpaRepository<ProgramModality, String> {

    boolean existsByModalityCode(String modalityCode);

    Optional<ProgramModality> findByModalityCode(String modalityCode);

    List<ProgramModality> findByProgramLevel(ProgramLevel programLevel);

    boolean existsByProgramLevel(ProgramLevel level);
}