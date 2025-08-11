package Henok.example.DeutscheCollageBack_endAPI.Entity;

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

    @Column(nullable = false)
    private Integer totalCrHr;

    @Column(nullable = true)
    private String deptHeadName;

    @Column(nullable = true)
    private String officePhoneNum;

    @Column(nullable = true)
    private String mobilePhoneNum;

    @Column(nullable = false)
    private String departmentCode;
}
