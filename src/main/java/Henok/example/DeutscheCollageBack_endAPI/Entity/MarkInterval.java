package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mark_interval")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkInterval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String description; // Optional description for the interval

    @Column(nullable = false)
    private double min; // Minimum score for this interval (e.g., 90.0)

    @Column(nullable = false)
    private double max; // Maximum score for this interval (e.g., 100.0)

    @Column(name = "given_value", nullable = false)
    private double givenValue; // GPA value for this interval (e.g., 4.0 for A+)

    @Column(name = "grade_letter", nullable = false)
    private String gradeLetter; // Letter grade (e.g., "A+", "B")

    @ManyToOne
    @JoinColumn(name = "grading_system_id", nullable = false)
    private GradingSystem gradingSystem; // Links to parent version

    // Explanation: Each interval belongs to one grading system version; intervalName removed, description added.
    // Database best practice: Foreign key ensures referential integrity; index on grading_system_id for fast queries.
}