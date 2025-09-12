package Henok.example.DeutscheCollageBack_endAPI.Entity;

import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Specific user to receive notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Role of user who triggered the action (e.g., DEAN for grade approval)
    @Enumerated(EnumType.STRING)
    @Column(name = "sender_role", nullable = false)
    private Role senderRole;

    // Notification message
    @Column(nullable = false, length = 500)
    private String message;

    // Creation timestamp
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    // Read status; defaults to false
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    // Why no targetRole: Removed as per request; role-based targeting handled in service.
    // Why user non-nullable: Ensures every notification is tied to a user for read tracking.
    // Database: Indexes for fast queries; FK constraint on user_id for integrity.
}
