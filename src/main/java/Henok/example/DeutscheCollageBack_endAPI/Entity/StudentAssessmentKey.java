package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAssessmentKey implements Serializable {

    private Long studentID;
    private Long assessmentID;
}

