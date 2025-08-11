package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;


import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "attrition_cause")
@Getter
@Setter
public class AttritionCause {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String attritionCause;
}