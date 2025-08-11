package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Student student;

    @ManyToOne
    @MapsId("assessmentID")
    @JoinColumn(name = "assessmentID", nullable = false)
    private Assessment assessment;

    @Column(nullable = false)
    private Double score;
}
