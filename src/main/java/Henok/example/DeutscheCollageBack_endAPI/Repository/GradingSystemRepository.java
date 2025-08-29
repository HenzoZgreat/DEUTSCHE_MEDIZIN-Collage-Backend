package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.GradingSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradingSystemRepository extends JpaRepository<GradingSystem, Long> {

    Optional<GradingSystem> findByVersionName(String versionName);

    // Explanation: Custom query method to find by version name for uniqueness checks.
    // Why: Supports validation in service layer to prevent duplicate version names.
}