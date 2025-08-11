package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assID;

    @ManyToOne
    @JoinColumn(name = "courseID", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String assTitle;

    @Column(nullable = false)
    private Double maxScore;
}

