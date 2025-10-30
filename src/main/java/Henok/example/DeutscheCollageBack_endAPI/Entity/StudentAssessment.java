package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAssessment {

    @EmbeddedId
    private StudentAssessmentKey id;

    @ManyToOne
    @MapsId("studentID")
    @JoinColumn(name = "studentID", nullable = false)
    private StudentDetails student;

    @ManyToOne
    @MapsId("assessmentID")
    @JoinColumn(name = "assessmentID", nullable = false)
    private Assessment assessment;

    @Column(nullable = false)
    private Double score;

    // Optional: when was it graded?
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
}
