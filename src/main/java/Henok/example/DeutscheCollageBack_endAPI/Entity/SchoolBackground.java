package Henok.example.DeutscheCollageBack_endAPI.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "School_Background_T")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolBackground {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String background;
}
