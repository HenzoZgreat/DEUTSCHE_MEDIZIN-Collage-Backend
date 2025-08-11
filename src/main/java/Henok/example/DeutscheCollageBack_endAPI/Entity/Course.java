package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cID;

    @Column(nullable = false)
    private String cTitle;

    @Column(nullable = false)
    private String cCode;

    @Column(nullable = false)
    private Integer crHr;

    @ManyToOne
    @JoinColumn(name = "cCatagoryID", nullable = false)
    private CourseCategory category;

    @ManyToOne
    @JoinColumn(name = "departmentID", nullable = false)
    private Department department;
}
