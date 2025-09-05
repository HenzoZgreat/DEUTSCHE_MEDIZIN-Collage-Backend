package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.AppliedStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppliedStudentRepository extends JpaRepository<AppliedStudent, Long> {

    /**
     * Checks if an applicant exists with the given phone number.
     * @param phoneNumber The phone number to check.
     * @return True if an applicant exists with the phone number.
     */
    boolean existsByPhoneNumber(String phoneNumber);
}