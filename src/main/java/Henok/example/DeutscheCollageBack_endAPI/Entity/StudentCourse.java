package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_course")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentDetails student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "batch_class_year_semester_id", nullable = false)
    private BatchClassYearSemester batchClassYearSemester;

    @ManyToOne
    @JoinColumn(name = "source_id", nullable = false)
    private CourseSource courseSource;

    @Column
    private Double score;

    @Setter
    @Column(nullable = false)
    private boolean isReleased = false;

}