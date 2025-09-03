package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grading_system")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradingSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String versionName; // e.g., "2023 Standard", "2025 Revised" – human-readable name for admin reference

    @Column(nullable = false)
    private LocalDate effectiveDate; // Date this system starts applying, retained but unused for grade resolution

    @Column(nullable = true)
    private String remark; // Optional remark or note about the grading system

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
    private Department department; // Optional: Links to department for department-specific systems; null for global

    @OneToMany(mappedBy = "gradingSystem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MarkInterval> intervals = new ArrayList<>(); // List of intervals for this version

    // Explanation: Entity groups intervals to version the grading scale, with optional department and remark.
    // Why: Supports department-specific grading; nullable remark for notes; effectiveDate retained but not used for resolution.
}