package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MarkInterval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarkIntervalRepository extends JpaRepository<MarkInterval, Long> {

    List<MarkInterval> findByGradingSystemId(Long gradingSystemId);

    // Explanation: Retrieves all intervals for a specific grading system.
    // Why: Useful for service layer when fetching or validating a full grading system.
}