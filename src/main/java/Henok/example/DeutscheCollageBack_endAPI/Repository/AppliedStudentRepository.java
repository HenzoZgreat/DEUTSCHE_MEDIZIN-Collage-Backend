package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.AppliedStudent;
import Henok.example.DeutscheCollageBack_endAPI.Enums.ApplicationStatus;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AppliedStudentRepository extends JpaRepository<AppliedStudent, Long> {

    /**
     * Checks if an applicant exists with the given phone number.
     * @param phoneNumber The phone number to check.
     * @return True if an applicant exists with the phone number.
     */
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT a.gender, COUNT(a) FROM AppliedStudent a GROUP BY a.gender")
    Map<Gender, Long> countByGenderGrouped();

    long countByApplicationStatus(ApplicationStatus status);

    List<AppliedStudent> findTop10ByOrderByIdDesc();

    long countByGender(Gender gender);

    //==================================================================================================================
    // ==================== NEW METHODS FOR REGISTRAR DASHBOARD ====================

    // Total applied students (redundant with count() but kept for consistency)
    long count();

    // Count applications grouped by ApplicationStatus
    // Returns List of nested projection for easy mapping to DTO
    @Query("SELECT new map(a.applicationStatus AS status, COUNT(a) AS count) " +
            "FROM AppliedStudent a " +
            "GROUP BY a.applicationStatus")
    List<Map<String, Object>> countByApplicationStatusGrouped();


    // Projection interface used above
    interface ApplicationStatusCountProjection {
        ApplicationStatus getStatus();
        Long getCount();
    }

    // Count applied students grouped by department
    @Query("SELECT new map(d.deptName AS departmentName, COUNT(a) AS count) " +
            "FROM AppliedStudent a " +
            "JOIN a.departmentEnrolled d " +
            "GROUP BY d.deptName")
    List<Map<String, Object>> countAppliedByDepartmentRaw();

    // Typed projection for department counts
    @Query("SELECT d.deptName AS departmentName, COUNT(a) AS count " +
            "FROM AppliedStudent a " +
            "JOIN a.departmentEnrolled d " +
            "GROUP BY d.deptName")
    List<DepartmentCountProjection> countAppliedByDepartment();

    interface DepartmentCountProjection {
        String getDepartmentName();
        Long getCount();
    }

}

