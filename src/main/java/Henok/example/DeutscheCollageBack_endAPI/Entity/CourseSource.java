package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sourceID;

    @Column(nullable = false)
    private String sourceName;
}
