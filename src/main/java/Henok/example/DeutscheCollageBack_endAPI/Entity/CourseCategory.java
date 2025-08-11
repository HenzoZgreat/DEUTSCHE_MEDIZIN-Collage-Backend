package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long catID;

    @Column(nullable = false)
    private String catName;
}
