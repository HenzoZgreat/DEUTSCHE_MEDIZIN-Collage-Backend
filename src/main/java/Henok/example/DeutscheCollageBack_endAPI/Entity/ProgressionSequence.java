package Henok.example.DeutscheCollageBack_endAPI.Entity;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "progression_sequence",
        uniqueConstraints = {
                // Per-department uniqueness
                @UniqueConstraint(
                        columnNames = {"department_id", "class_year_id", "semester_id"},
                        name = "uk_progression_dept_classyear_semester"
                ),
                // Global uniqueness (department_id IS NULL)
                @UniqueConstraint(
                        columnNames = {"class_year_id", "semester_id"},
                        name = "uk_progression_global_classyear_semester"
                )
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressionSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nullable → null means GLOBAL progression rule (default for all departments)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = true)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_year_id", nullable = false)
    private ClassYear classYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(nullable = false)
    private Integer sequenceNumber;

    @Column(length = 150)
    private String description;
}