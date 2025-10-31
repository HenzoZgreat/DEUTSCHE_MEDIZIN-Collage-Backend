package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentDetails;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Long> {
    Optional<StudentDetails> findByPhoneNumber(String phoneNumber);

    Optional<StudentDetails> findByUser(User student);

    // Finds all students with enabled user accounts
    // Why: Ensures only active students are retrieved for most queries
    List<StudentDetails> findAllByUserEnabledTrue();

    // Finds a student by ID where the user account is enabled
    // Why: Ensures retrieval respects the enabled flag for consistency
    Optional<StudentDetails> findByIdAndUserEnabledTrue(Long id);

    // Checks if a phone number is already in use by another student
    // Why: Enforces uniqueness constraint at the application level
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    // Finds a student by associated user ID
    // Why: Useful for linking user authentication to student details
    Optional<StudentDetails> findByUserId(Long userId);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByExitExamUserID(String exitExamUserID);

}
