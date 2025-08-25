package Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.EnrollmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EnrollmentTypeRepository extends JpaRepository<EnrollmentType, String> {

    boolean existsByEnrollmentTypeCode(String enrollmentTypeCode);

    Optional<EnrollmentType> findByEnrollmentTypeCode(String enrollmentTypeCode);
}
