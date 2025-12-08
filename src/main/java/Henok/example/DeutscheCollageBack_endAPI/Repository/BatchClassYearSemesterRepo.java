package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Batch;
import Henok.example.DeutscheCollageBack_endAPI.Entity.BatchClassYearSemester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.ClassYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BatchClassYearSemesterRepo extends JpaRepository<BatchClassYearSemester, Long> {
    
    /**
     * Finds a BatchClassYearSemester by batch, classYear, and semester.
     * @param batch The batch
     * @param classYear The class year
     * @param semester The semester
     * @return Optional BatchClassYearSemester
     */
    Optional<BatchClassYearSemester> findByBatchAndClassYearAndSemester(Batch batch, ClassYear classYear, Semester semester);
}
