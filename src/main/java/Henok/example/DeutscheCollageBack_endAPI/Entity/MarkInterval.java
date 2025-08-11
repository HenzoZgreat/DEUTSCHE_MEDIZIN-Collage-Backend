package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Mark_interval_T")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkInterval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interval_name", nullable = false)
    private String intervalName;

    @Column(nullable = false)
    private double min;

    @Column(nullable = false)
    private double max;

    @Column(name = "given_value", nullable = false)
    private double givenValue; // e.g. 4.0, 3.5

    @Column(name = "grade_letter", nullable = false)
    private String gradeLetter; // e.g. A+, A, B+
}

