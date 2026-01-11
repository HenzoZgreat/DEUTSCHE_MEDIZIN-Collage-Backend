package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // Fetch users by role
    List<User> findByRole(Role role);

    // Check if user exists by ID
    boolean existsById(Long id);

    // Checks if a username is already taken during registration/update.
    boolean existsByUsername(String username);

    long countByRole(Role role);

    // Custom projection query — only fetches id and username
    @Query("SELECT u.id, u.username FROM User u WHERE u.role = :role")
    List<Object[]> findUserIdAndUsernameByRole(@Param("role") Role role);
}