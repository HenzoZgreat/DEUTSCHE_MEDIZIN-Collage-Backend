package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.DeanViceDeanDetails;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeanViceDeanDetailsRepository extends JpaRepository<DeanViceDeanDetails, Long> {

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    // findByUserRoleAndActiveTrue: Custom query to fetch by role and active status.
    @Query("SELECT d FROM DeanViceDeanDetails d WHERE d.user.role = :role AND d.active = true")
    List<DeanViceDeanDetails> findByUserRoleAndActiveTrue(@Param("role") Role role);

    // findByUser: To fetch details by User for profile.
    Optional<DeanViceDeanDetails> findByUser(User user);
}
