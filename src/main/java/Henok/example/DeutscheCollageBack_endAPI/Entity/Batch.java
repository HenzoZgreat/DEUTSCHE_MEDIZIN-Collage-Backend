package Henok.example.DeutscheCollageBack_endAPI.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Batchs_T")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long batchId;

    @Column(name = "batch_name", nullable = false)
    private int batchName; // you specified batchName should be int
}
