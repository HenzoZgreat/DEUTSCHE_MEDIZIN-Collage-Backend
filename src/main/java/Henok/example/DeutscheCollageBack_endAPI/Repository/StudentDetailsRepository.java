package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentDetails;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Long> {
    Optional<StudentDetails> findByPhoneNumber(String phoneNumber);

    Optional<StudentDetails> findByUser(User student);
}
