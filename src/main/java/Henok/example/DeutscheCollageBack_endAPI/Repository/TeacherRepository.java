package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherDetail, Long> {
    Optional<TeacherDetail> findByUser(User user);
    Optional<TeacherDetail> findByPhoneNumber(String phoneNumber);
}