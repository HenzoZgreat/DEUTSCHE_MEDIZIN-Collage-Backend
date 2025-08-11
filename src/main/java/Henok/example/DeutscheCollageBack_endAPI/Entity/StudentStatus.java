package Henok.example.DeutscheCollageBack_endAPI.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Status_T")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long id;

    @Column(name = "status_name", nullable = false)
    private String statusName;
}
