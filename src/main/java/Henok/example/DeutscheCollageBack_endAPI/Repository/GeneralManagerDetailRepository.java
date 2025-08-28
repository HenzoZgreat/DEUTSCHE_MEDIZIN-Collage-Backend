package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.GeneralManagerDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneralManagerDetailRepository extends JpaRepository<GeneralManagerDetail, Long> {
    Optional<GeneralManagerDetail> findByPhoneNumber(String phoneNumber);

    Optional<GeneralManagerDetail> findByUser(User user);
}