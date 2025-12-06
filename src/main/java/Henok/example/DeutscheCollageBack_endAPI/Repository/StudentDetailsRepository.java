package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Department;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentDetails;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Fetches all students by a list of IDs with necessary relations eagerly loaded
    @Query("""
        SELECT DISTINCT s FROM StudentDetails s
        JOIN FETCH s.user u
        JOIN FETCH s.departmentEnrolled d
        JOIN FETCH s.programModality pm
        JOIN FETCH s.batchClassYearSemester bcys
        JOIN FETCH bcys.classYear cy
        JOIN FETCH bcys.semester sem
        LEFT JOIN FETCH bcys.entryYear ay
        WHERE s.id IN :ids
        """)
    List<StudentDetails> findAllByIdInWithRelations(@Param("ids") List<Long> ids);

    // Finds distinct departments for a list of student IDs
    // Why: Efficiently retrieves unique departments for slip generation
    @Query("""
    SELECT DISTINCT s.departmentEnrolled 
    FROM StudentDetails s 
    WHERE s.id IN :studentIds 
    AND s.departmentEnrolled IS NOT NULL
    """)
    List<Department> findDistinctDepartmentsByStudentIds(@Param("studentIds") List<Long> studentIds);

}
