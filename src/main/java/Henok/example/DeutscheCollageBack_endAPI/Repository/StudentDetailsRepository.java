package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Long> {
}
