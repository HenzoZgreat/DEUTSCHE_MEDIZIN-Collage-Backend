package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepo extends JpaRepository<Department, Long> {
}
