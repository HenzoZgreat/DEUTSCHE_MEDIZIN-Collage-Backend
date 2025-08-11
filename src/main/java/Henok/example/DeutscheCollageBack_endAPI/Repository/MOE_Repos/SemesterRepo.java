package Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SemesterRepo extends JpaRepository<Semester, String> {
}
