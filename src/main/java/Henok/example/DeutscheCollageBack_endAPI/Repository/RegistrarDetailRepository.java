package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.RegistrarDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrarDetailRepository extends JpaRepository<RegistrarDetail, Long> {
    Optional<RegistrarDetail> findByPhoneNumber(String phoneNumber);

    Optional<RegistrarDetail> findByUser(User user);
}
