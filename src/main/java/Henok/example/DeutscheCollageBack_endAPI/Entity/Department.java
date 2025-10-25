package Henok.example.DeutscheCollageBack_endAPI.Entity;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.ProgramModality;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dptID;

    @Column(nullable = false)
    private String deptName;

    @Column(nullable = true)
    private Integer totalCrHr;

    @Column(nullable = false)
    private String departmentCode;

    @ManyToOne
    @JoinColumn(name = "modality_code", nullable = true)
    private ProgramModality programModality;
}
