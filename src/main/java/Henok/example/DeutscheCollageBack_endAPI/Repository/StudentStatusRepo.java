package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentStatusRepo extends JpaRepository<StudentStatus, Long> {
}
