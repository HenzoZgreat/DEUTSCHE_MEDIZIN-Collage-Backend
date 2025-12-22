package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Entity.DepartmentHeadDetails;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentHeadRepository extends JpaRepository<DepartmentHeadDetails, Long> {
    boolean existsByDepartment(Department department);
    Optional<DepartmentHeadDetails> findByUser(User user);
    Optional<DepartmentHeadDetails> findByDepartment(Department department);
}
