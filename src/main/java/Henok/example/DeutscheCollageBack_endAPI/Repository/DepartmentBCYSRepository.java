package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.BatchClassYearSemester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Entity.DepartmentBCYS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentBCYSRepository extends JpaRepository<DepartmentBCYS, Long> {

    // Find all department links for a specific BCYS
    List<DepartmentBCYS> findByBcys(BatchClassYearSemester bcys);

    // Find all department links for a specific BCYS by its ID
    List<DepartmentBCYS> findByBcys_BcysID(Long bcysId);

    // Check if a specific department is already linked to this BCYS
    boolean existsByBcysAndDepartment(BatchClassYearSemester bcys, Department department);

    // Delete all links for a given BCYS (useful during delete or full replace)
    void deleteByBcys(BatchClassYearSemester bcys);

    // Optional: find by bcys and department (for checking before add/update)
    Optional<DepartmentBCYS> findByBcysAndDepartment(BatchClassYearSemester bcys, Department department);
}
