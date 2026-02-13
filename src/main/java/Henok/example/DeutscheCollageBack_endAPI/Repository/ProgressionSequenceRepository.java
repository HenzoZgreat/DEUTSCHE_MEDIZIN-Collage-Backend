package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.ClassYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import Henok.example.DeutscheCollageBack_endAPI.Entity.ProgressionSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository for ProgressionSequence entity.
// Why: Provides methods to lookup sequences by department + classyear/semester combos.
// Includes bulk find for performance (e.g., pre-load all for a dept in CGPA calc).
@Repository
public interface ProgressionSequenceRepository extends JpaRepository<ProgressionSequence, Long> {
    // Lookup specific (department not null)
    Optional<ProgressionSequence> findByDepartmentAndClassYearAndSemester(
            Department department, ClassYear classYear, Semester semester);

    // Lookup global (department is null)
    Optional<ProgressionSequence> findByDepartmentIsNullAndClassYearAndSemester(
            ClassYear classYear, Semester semester);

    // All rules for a specific department
    List<ProgressionSequence> findByDepartment(Department department);

    // All global rules
    List<ProgressionSequence> findByDepartmentIsNull();
}