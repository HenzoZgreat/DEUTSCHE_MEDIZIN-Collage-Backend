package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// TeacherCourseAssignment.java
@Entity
@Table(
        name = "teacher_course_assignment",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"teacher_id", "course_id", "bcys_id"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourseAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherDetail teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bcys_id", nullable = false)
    private BatchClassYearSemester bcys;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();
}