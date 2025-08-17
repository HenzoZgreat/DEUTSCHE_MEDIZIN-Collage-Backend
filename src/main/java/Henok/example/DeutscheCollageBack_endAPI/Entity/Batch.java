package Henok.example.DeutscheCollageBack_endAPI.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "batch")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_name", nullable = false, unique = true)
    private String batchName;
}
