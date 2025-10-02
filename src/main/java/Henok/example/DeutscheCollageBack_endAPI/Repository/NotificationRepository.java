package Henok.example.DeutscheCollageBack_endAPI.Repository;

import Henok.example.DeutscheCollageBack_endAPI.Entity.Notification;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Fetch all notifications for a user, ordered by creation date (newest first)
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Fetch a specific notification for a user by ID
    Optional<Notification> findByIdAndUser(Long id, User user);

    // Add to existing NotificationRepository interface
    @Query("SELECT n FROM Notification n WHERE n.user = :user ORDER BY n.createdAt DESC")
    List<Notification> findTop5ByUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);

    // Purpose: Retrieves the latest 5 notifications for a user, sorted by creation date descending.
    // Why Pageable: Limits to 5 records efficiently at the database level.

    // Purpose: Supports retrieving user-specific notifications and validating ownership for mark-as-read.
    // Why findByIdAndUser: Ensures user can only mark their own notifications.
}