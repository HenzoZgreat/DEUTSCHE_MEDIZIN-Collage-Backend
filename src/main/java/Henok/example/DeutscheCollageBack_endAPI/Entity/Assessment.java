package Henok.example.DeutscheCollageBack_endAPI.Entity;

import Henok.example.DeutscheCollageBack_endAPI.Enums.AssessmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "assessment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_course_assignment_id", nullable = false)
    private TeacherCourseAssignment teacherCourseAssignment;

    @Column(name = "ass_title", nullable = false, length = 150)
    private String assTitle;

    @Column(name = "max_score", nullable = false)
    private Double maxScore;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ass_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AssessmentStatus assStatus = AssessmentStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}