package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Course;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepo extends JpaRepository<Course, Long> {
    boolean existsBycCode(String cCode);

    List<Course> findByDepartment(Department department);

    // Custom query to find distinct courses by a list of departments
    @Query("SELECT DISTINCT c FROM Course c WHERE c.department IN :departments")
    List<Course> findDistinctByDepartmentIn(@Param("departments") List<Department> departments);
}