package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.RegistrarDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrarDetailRepo extends JpaRepository<RegistrarDetail, Long> {
    boolean existsByUser(User user);
    boolean existsByEmail(String email);
}
