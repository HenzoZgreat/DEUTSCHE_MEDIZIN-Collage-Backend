package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "program_level")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramLevel {
    // Unique code serving as the primary identifier (e.g., "DEG" for Degree, "DIP" for Diploma)
    // Why: String-based ID for easy reference and integration with external systems
    @Id
    private String code;
    // Descriptive name of the program level (e.g., "Bachelor's Degree", "Diploma")
    // Why: Provides a human-readable label for the program level
    @Column(nullable = false)
    private String name;
    // Additional remarks or information about the program level
    // Why: Allows for extra context, such as requirements or notes
    @Column
    private String remark;
    // Flag indicating if this program level is currently active/offered
    // Why: Enables soft deletion or toggling availability without removing records
    @Column(nullable = false)
    private Boolean active = true;
}