package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramLevel;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramModality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepo extends JpaRepository<Department, Long> {
    boolean existsByDepartmentCode(String departmentCode);

    Department findById(long dptID);

    List<Department> findByProgramModality(ProgramModality programModality);

    List<Department> findByProgramLevel(ProgramLevel programLevel);

    boolean existsByProgramLevel(ProgramLevel level);

    boolean existsByProgramModality(ProgramModality modality);
}
