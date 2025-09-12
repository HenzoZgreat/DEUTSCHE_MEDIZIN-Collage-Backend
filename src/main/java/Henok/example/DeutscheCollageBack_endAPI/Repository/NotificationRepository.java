package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Notification;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Fetch all notifications for a user, ordered by creation date (newest first)
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Fetch a specific notification for a user by ID
    Optional<Notification> findByIdAndUser(Long id, User user);

    // Purpose: Supports retrieving user-specific notifications and validating ownership for mark-as-read.
    // Why findByIdAndUser: Ensures user can only mark their own notifications.
}