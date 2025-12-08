package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Entity.GradingSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradingSystemRepository extends JpaRepository<GradingSystem, Long> {

    Optional<GradingSystem> findByVersionName(String versionName);

    @Query("SELECT g FROM GradingSystem g WHERE (:department IS NULL AND g.department IS NULL OR g.department = :department) ORDER BY g.id DESC")
    Page<GradingSystem> findLatestByDepartment(@Param("department") Department department, Pageable pageable);

    @Query("SELECT g FROM GradingSystem g ORDER BY g.effectiveDate DESC")
    Page<GradingSystem> findLatestByEffectiveDate(Pageable pageable);

    @Query("SELECT g FROM GradingSystem g WHERE g.isActive = true AND (:department IS NULL AND g.department IS NULL OR g.department = :department)")
    List<GradingSystem> findActiveByDepartment(@Param("department") Department department);

    // Explanation: Custom query to find the latest grading system for a department (or global) sorted by id DESC.
    // Why: Returns Page for proper pagination; pageable limits to top 1 result for latest system.
}