package Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;


interface AcademicTermRepository extends JpaRepository<AcademicTerm, String> {
}