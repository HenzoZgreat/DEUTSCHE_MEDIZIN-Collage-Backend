package Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenderRepo extends JpaRepository<Gender, String> {
}
