package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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
    private Integer theoryHrs = 0;

    @Column(nullable = false)
    private Integer labHrs = 0;

    @ManyToOne
    @JoinColumn(name = "cCatagoryID", nullable = false)
    private CourseCategory category;

    @ManyToOne
    @JoinColumn(name = "departmentID", nullable = false)
    private Department department;

    @ManyToMany
    @JoinTable(
            name = "course_prerequisites",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    private Set<Course> prerequisites = new HashSet<>();
}
