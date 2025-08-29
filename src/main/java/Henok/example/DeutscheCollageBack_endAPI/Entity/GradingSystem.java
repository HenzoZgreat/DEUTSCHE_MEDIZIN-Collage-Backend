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
    private LocalDate effectiveDate; // Date this system starts applying, for historical tracking

    @OneToMany(mappedBy = "gradingSystem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MarkInterval> intervals = new ArrayList<>(); // List of intervals for this version

    // Explanation: This entity groups intervals to version the grading scale.
    // Why: Prevents global changes; each version is immutable once created.
}
